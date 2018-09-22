package matchup.sim;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import matchup.sim.utils.*;

public class Simulator {
    private static final String root = "matchup";
    private static final String statics_root = "statics";

    private static boolean gui = false;

    private static double fps = 1;
    private static int n_games = 1;
    private static String playerAName;
    private static String playerBName;

    private static PlayerWrapper playerA;
    private static PlayerWrapper playerB;

    private static boolean isHome = true;

    private static List<Game> games = new ArrayList<Game>();

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        parseArgs(args);

        try {
            playerA = loadPlayerWrapper(playerAName);
            playerB = loadPlayerWrapper(playerBName);
        } catch (Exception ex) {
            System.out.println("Unable to load players. " + ex.getMessage());
            System.exit(0);
        }

        HTTPServer server = null;
        if (gui) {
            server = new HTTPServer();
            Log.record("Hosting HTTP Server on " + server.addr());
            if (!Desktop.isDesktopSupported())
                Log.record("Desktop operations not supported");
            else if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                Log.record("Desktop browse operation not supported");
            else {
                try {
                    Desktop.getDesktop().browse(new URI("http://localhost:" + server.port()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        isHome = true;

        for (int j=0; j < n_games; ++j) {
            System.out.println("\nStarting game with players " + playerA.getName() + ", " + playerB.getName());

            try {
                playerA.init(playerBName);
                playerB.init(playerAName);

                List<Integer> skillsA = new ArrayList<Integer>(playerA.getSkills());
                List<Integer> skillsB = new ArrayList<Integer>(playerB.getSkills());

                for (int i = 0; i < 2; ++i)
                {
                    Game game = new Game(playerAName, playerBName, isHome);
                    games.add(game);

                    System.out.println("\nHome: " +
                        (isHome ? playerA.getName() : playerB.getName()) +
                        ", Away: " + (!isHome ? playerA.getName() : playerB.getName()));

                    game.playerA.skills = skillsA;
                    game.playerB.skills = skillsB;

                    game.playerA.distribution = getClone(playerA.getDistribution(skillsB, isHome));
                    game.playerB.distribution = getClone(playerB.getDistribution(skillsA, !isHome));

                    List<Integer> roundA = null;
                    List<Integer> roundB = null;

                    for (int turn = 0; turn < 3; ++turn) {
                        System.out.println("\nRound " + (turn + 1));

                        if (isHome) {
                            roundB = playerB.playRound(roundA);
                            roundA = playerA.playRound(roundB);
                        }
                        else {
                            roundA = playerA.playRound(roundB);
                            roundB = playerB.playRound(roundA);
                        }

                        game.playerA.rounds.add(new ArrayList<Integer>(roundA));
                        game.playerB.rounds.add(new ArrayList<Integer>(roundB));

                        int[] scores = getScores(roundA, roundB);
                        game.playerA.score += scores[0];
                        game.playerB.score += scores[1];

                        System.out.println("Score: " + playerA.getName() + " " + scores[0] +
                            ", " + playerB.getName() + " " + scores[1]);

                        if (gui) {
                            gui(server, state(i == 1 && turn == 2 ? -1 : fps, games));
                        }
                    }

                    int[] totalScores = getTotalScores(games);

                    System.out.println("\nTotal score: " + playerA.getName() +
                        " " + totalScores[0] + " " + playerB.getName() +
                        " " + totalScores[1]);

                    swapPlayers();
                }
            } catch (Exception ex) {
                System.out.println("Exception! " + ex.getMessage());
                System.exit(0);
            }

            int[] totalScores = getTotalScores(games);

            if (totalScores[0] != totalScores[1]) {
                System.out.println("\nWinner: " +
                    (totalScores[0] > totalScores[1] ?
                        playerA.getName() + " " + totalScores[0] :
                        playerB.getName() + " " + totalScores[1]));
            }
            else {
                System.out.println("\nTie! Score: " + totalScores[0]);
            }
        }

        printStats();

        System.exit(0);
    }

    public static void printStats() {
        int playerAAwayScores = 0;
        int playerAHomeScores = 0;
        int playerBAwayScores = 0;
        int playerBHomeScores = 0;

        int playerAWins = 0;
        int playerBWins = 0;
        int ties = 0;

        int totalA = 0;
        int totalB = 0;

        int counter = 1;

        for (Game g : games) {
            if (g.playerA.isHome) {
                playerAHomeScores += g.playerA.score;
                playerBAwayScores += g.playerB.score;

                totalA += g.playerA.score;
                totalB += g.playerB.score;
            }
            else {
                playerAAwayScores += g.playerA.score;
                playerBHomeScores += g.playerB.score;

                totalA += g.playerA.score;
                totalB += g.playerB.score;
            }

            if (counter % 2 == 0) {
                if (totalA > totalB) {
                    playerAWins += 1;
                }
                else if (totalA == totalB){
                    ties += 1;
                }
                else {
                    playerBWins += 1;
                }

                totalA = 0;
                totalB = 0;
            }

            counter += 1;
        }

        System.out.println("\n******** Results ********");
        System.out.println("\nTotal wins: ");
        System.out.println(playerAName + ": " + playerAWins);
        System.out.println(playerBName + ": " + playerBWins);
        System.out.println("\nTies: " + ties);
        System.out.println("\nTotal scores: ");
        System.out.println(playerAName + ": " + (playerAHomeScores + playerAAwayScores));
        System.out.println(playerBName + ": " + (playerBHomeScores + playerBAwayScores));
        System.out.println("\nAvg scores as Home: ");
        System.out.println(playerAName + ": " + ((double)playerAHomeScores*2/games.size()));
        System.out.println(playerBName + ": " + ((double)playerBHomeScores*2/games.size()));
        System.out.println("\nAvg scores as Away: ");
        System.out.println(playerAName + ": " + ((double)playerAAwayScores*2/games.size()));
        System.out.println(playerBName + ": " + ((double)playerBAwayScores*2/games.size()));
    }

    private static List<List<Integer>> getClone(List<List<Integer>> lol) {
        List<List<Integer>> newLol = new ArrayList<>();
        for (List<Integer> l : lol) {
            newLol.add(new ArrayList<Integer>(l));
        }

        return newLol;
    }

    public static List<Game> getGames() {
        List<Game> cloneGames = new ArrayList<Game>();
        for (Game g : games) {
            cloneGames.add(deepClone(g));
        }

        return cloneGames;
    }

    public static Game getLastGame() {
        return deepClone(games.get(games.size() - 1));
    }

    private static Game deepClone(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
            return (Game) objectInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[] getScores(List<Integer> roundA, List<Integer> roundB) {
        int[] scores = new int[2];
        for (int i=0; i<5; ++i) {
            if (roundA.get(i) - roundB.get(i) > 2) {
                ++scores[0];
            }
            else if (roundB.get(i) - roundA.get(i) > 2) {
                ++scores[1];
            }
        }

        return scores;
    }

    private static int[] getTotalScores(List<Game> games) {
        if (games.size() % 2 != 0) {
            return new int[] { 
                games.get(games.size() - 1).playerA.score, 
                games.get(games.size() - 1).playerB.score };
        }

        return new int[] {
            games.get(games.size() - 1).playerA.score + games.get(games.size() - 2).playerA.score, 
            games.get(games.size() - 1).playerB.score + games.get(games.size() - 2).playerB.score};
    }

    private static void swapPlayers() {
        playerA.clear();
        playerB.clear();

        isHome = !isHome;
    }

    private static PlayerWrapper loadPlayerWrapper(String name) throws Exception {
        Log.record("Loading player " + name);
        Player p = loadPlayer(name);
        if (p == null) {
            Log.record("Cannot load player " + name);
            System.exit(1);
        }

        return new PlayerWrapper(p, name);
    }

    // The state that is sent to the GUI. (JSON)
    private static String state(double fps, List<Game> games) {
        Game game1 = null;
        Game game2 = null;

        List<Integer> roundA;
        List<Integer> roundB;

        if (games.size() % 2 == 0) {
            game1 = games.get(games.size() - 2);
            game2 = games.get(games.size() - 1);

            roundA = game2.playerA.rounds.get(game2.playerA.rounds.size() - 1);
            roundB = game2.playerB.rounds.get(game2.playerB.rounds.size() - 1);
        }
        else {
            game1 = games.get(games.size() - 1);
            roundA = game1.playerA.rounds.get(game1.playerA.rounds.size() - 1);
            roundB = game1.playerB.rounds.get(game1.playerB.rounds.size() - 1);
        }

        List<String> distrib_a1 = new ArrayList<String>();
        List<String> distrib_b1 = new ArrayList<String>();
        List<String> distrib_a2 = new ArrayList<String>();
        List<String> distrib_b2 = new ArrayList<String>();
        
        for (int i=0; i<3; ++i) {
            distrib_a1.add(join(", ", game1.playerA.distribution.get(i)));
            distrib_b1.add(join(", ", game1.playerB.distribution.get(i)));

            if (game2 != null) {
                distrib_a2.add(join(", ", game2.playerA.distribution.get(i)));
                distrib_b2.add(join(", ", game2.playerB.distribution.get(i)));
            }
        }

        // Aaaaaaaaaaaaa!!!!
        String json = "{\"refresh\":" + (1000.0/fps) + ",\"is_home\":\"" + isHome +
            "\",\"grp_a\":\"" + game1.playerA.name +
            "\",\"grp_b\":\"" + game1.playerB.name +
            "\",\"grp_a_skills\":\"" + join(", ", game1.playerA.skills) +
            "\",\"grp_b_skills\":\"" + join(", ", game1.playerB.skills) +
            "\",\"grp_a_round\":\"" + join(",", roundA) +
            "\",\"grp_b_round\":\"" + join(",", roundB) +
            "\",\"grp_a_dist1\":\"" + String.join(";", distrib_a1) +
            "\",\"grp_b_dist1\":\"" + String.join(";", distrib_b1);

        if (distrib_a2.size() > 0) {
            json += "\",\"grp_a_dist2\":\"" + String.join(";", distrib_a2) +
            "\",\"grp_b_dist2\":\"" + String.join(";", distrib_b2);
        }

        int[] scores = getTotalScores(games);

        json += "\",\"grp_a_score\":\"" + scores[0] +
            "\",\"grp_b_score\":\"" + scores[1] + "\"}";

        //System.out.println(json);

        return json;
    }

    private static String join(String joins, List<Integer> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(joins));
    }

    private static void gui(HTTPServer server, String content) {
        if (server == null) return;
        String path = null;
        for (;;) {
            for (;;) {
                try {
                    path = server.request();
                    break;
                } catch (IOException e) {
                    Log.record("HTTP request error " + e.getMessage());
                }
            }
            if (path.equals("data.txt")) {
                try {
                    server.reply(content);
                } catch (IOException e) {
                    Log.record("HTTP dynamic reply error " + e.getMessage());
                }
                return;
            }
            if (path.equals("")) path = "webpage.html";
            else if (!Character.isLetter(path.charAt(0))) {
                Log.record("Potentially malicious HTTP request \"" + path + "\"");
                break;
            }

            File file = new File(statics_root + File.separator + path);
            if (file == null) {
                Log.record("Unknown HTTP request \"" + path + "\"");
            } else {
                try {
                    server.reply(file);
                } catch (IOException e) {
                    Log.record("HTTP static reply error " + e.getMessage());
                }
            }
        }
    }

    private static void parseArgs(String[] args) {
        int i = 0;
        List<String> playerNames = new ArrayList<String>();
        for (; i < args.length; ++i) {
            switch (args[i].charAt(0)) {
                case '-':
                    if (args[i].equals("-p") || args[i].equals("--players")) {
                        while (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                            ++i;
                            playerNames.add(args[i]);
                        }

                        if (playerNames.size() != 2) {
                            throw new IllegalArgumentException("Invalid number of players, you need 2 players to start a game.");
                        }

                        playerAName = playerNames.get(0);
                        playerBName = playerNames.get(1);
                    } else if (args[i].equals("-g") || args[i].equals("--gui")) {
                        gui = true;
                    } else if (args[i].equals("-l") || args[i].equals("--logfile")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing logfile name");
                        }
                        Log.setLogFile(args[i]);
                    } else if (args[i].equals("--fps")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing frames per second.");
                        }
                        fps = Double.parseDouble(args[i]);
                    } else if (args[i].equals("-n") || args[i].equals("--num_games")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing number of games.");
                        }
                        n_games = Integer.parseInt(args[i]);
                    } else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                        Log.activate();
                    } else {
                        throw new IllegalArgumentException("Unknown argument '" + args[i] + "'");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument '" + args[i] + "'");
            }
        }

        Log.record("Players: " + playerNames.toString());
        Log.record("GUI " + (gui ? "enabled" : "disabled"));

        if (gui)
            Log.record("FPS: " + fps);
    }

    private static Set<File> directory(String path, String extension) {
        Set<File> files = new HashSet<File>();
        Set<File> prev_dirs = new HashSet<File>();
        prev_dirs.add(new File(path));
        do {
            Set<File> next_dirs = new HashSet<File>();
            for (File dir : prev_dirs)
                for (File file : dir.listFiles())
                    if (!file.canRead()) ;
                    else if (file.isDirectory())
                        next_dirs.add(file);
                    else if (file.getPath().endsWith(extension))
                        files.add(file);
            prev_dirs = next_dirs;
        } while (!prev_dirs.isEmpty());
        return files;
    }

    public static Player loadPlayer(String name) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String sep = File.separator;
        Set<File> player_files = directory(root + sep + name, ".java");
        File class_file = new File(root + sep + name + sep + "Player.class");
        long class_modified = class_file.exists() ? class_file.lastModified() : -1;
        if (class_modified < 0 || class_modified < last_modified(player_files) ||
                class_modified < last_modified(directory(root + sep + "sim", ".java"))) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null)
                throw new IOException("Cannot find Java compiler");
            StandardJavaFileManager manager = compiler.
                    getStandardFileManager(null, null, null);
//            long files = player_files.size();
            Log.record("Compiling for player " + name);
            if (!compiler.getTask(null, manager, null, null, null,
                    manager.getJavaFileObjectsFromFiles(player_files)).call())
                throw new IOException("Compilation failed");
            class_file = new File(root + sep + name + sep + "Player.class");
            if (!class_file.exists())
                throw new FileNotFoundException("Missing class file");
        }
        ClassLoader loader = Simulator.class.getClassLoader();
        if (loader == null)
            throw new IOException("Cannot find Java class loader");
        @SuppressWarnings("rawtypes")
        Class raw_class = loader.loadClass(root + "." + name + ".Player");
        return (Player)raw_class.newInstance();
    }

    private static long last_modified(Iterable<File> files) {
        long last_date = 0;
        for (File file : files) {
            long date = file.lastModified();
            if (last_date < date)
                last_date = date;
        }
        return last_date;
    }
}
