package matchup.sim;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

class Game {
    public String player_a;
    public String player_b;

    public List<List<Integer>> player_aRounds;
    public List<List<Integer>> player_bRounds;

    public int player_aScore;
    public int player_bScore;

    public Game(String player_a, String player_b) {
        this.player_a = player_a;
        this.player_b = player_b;

        player_aRounds = new ArrayList<List<Integer>>();
        player_bRounds = new ArrayList<List<Integer>>();
    }
}

public class Simulator {
    private static final String root = "matchup";
    private static final String statics_root = "statics";

    private static final int turnLimit = 100000;

    private static boolean gui = false;
    private static boolean tournament = false;

    private static double fps = 1;
    private static int n_games = 1;
    private static String player_a_name;
    private static String player_b_name;

    private static PlayerWrapper player_a;
    private static PlayerWrapper player_b;

    private static boolean isHome = true;

    private static List<Game> games = new ArrayList<Game>();

    // Tournament variables.
    private static PlayerWrapper[] players;
    private static String[] player_names;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        parseArgs(args);

        if (!tournament) {
            try {
                player_a = loadPlayerWrapper(player_a_name);
                player_b = loadPlayerWrapper(player_b_name);
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

                List<List<Integer>> skills = new ArrayList<List<Integer>>();
                List<List<List<Integer>>> distribution = new ArrayList<List<List<Integer>>>();

                Game game = new Game(player_a_name, player_b_name);
                System.out.println("\nStarting game with players " + player_a.getName() + ", " + player_b.getName());

                try {
                    player_a.init(player_b_name);
                    player_b.init(player_a_name);

                    skills.add(player_a.getSkills());
                    skills.add(player_b.getSkills());

                    for (int i = 0; i < 2; ++i)
                    {
                        System.out.println("\nHome: " +
                            (isHome ? player_a.getName() : player_b.getName()) +
                            ", Away: " + (!isHome ? player_a.getName() : player_b.getName()));

                        distribution.add(player_a.getDistribution(skills.get(1), isHome));
                        distribution.add(player_b.getDistribution(skills.get(0), !isHome));

                        List<Integer> round_a = null;
                        List<Integer> round_b = null;

                        for (int turn = 0; turn < 3; ++turn) {
                        	System.out.println("\nRound " + (turn + 1));

                            if (isHome) {
                                round_b = player_b.playRound(round_a);
                                round_a = player_a.playRound(round_b);
                            }
                            else {
                                round_a = player_a.playRound(round_b);
                                round_b = player_b.playRound(round_a);
                            }

                            game.player_bRounds.add(round_b);
                            game.player_aRounds.add(round_a);

                            int[] scores = getScores(round_a, round_b);
                            game.player_aScore += scores[0];
                            game.player_bScore += scores[1];

                            System.out.println("Score: " + player_a.getName() + " " + scores[0] +
                                ", " + player_b.getName() + " " + scores[1]);

                        	if (gui) {
                                gui(server, state(i == 1 && turn == 2 ? -1 : fps, skills, distribution, game));
                            }
                        }

                        System.out.println("\nTotal score: " + player_a.getName() +
                            " " + game.player_aScore + " " + player_b.getName() +
                            " " + game.player_bScore);

                        swapPlayers();
                    }
                } catch (Exception ex) {
                    System.out.println("Exception! " + ex.getMessage());
                    System.exit(0);
                }

                if (game.player_aScore != game.player_bScore) {
                    System.out.println("\nWinner: " +
                        (game.player_aScore > game.player_bScore ?
                            player_a.getName() + " " + game.player_aScore :
                            player_b.getName() + " " + game.player_bScore));
                }
                else {
                    System.out.println("\nTie! Score: " + game.player_aScore);
                }

                games.add(game);
            }
        }

        System.exit(0);
    }

    public static int[] getScores(List<Integer> round_a, List<Integer> round_b) {
        int[] scores = new int[2];
        for (int i=0; i<5; ++i) {
            if (round_a.get(i) - round_b.get(i) > 2) {
                ++scores[0];
            }
            else if (round_b.get(i) - round_a.get(i) > 2) {
                ++scores[1];
            }
        }

        return scores;
    }

    public static void swapPlayers() {
        player_a.clear();
        player_b.clear();

        isHome = false;
    }

    private static PlayerWrapper loadPlayerWrapper(String name) throws Exception {
        Log.record("Loading player " + name);
        Player p = loadPlayer(name);
        if (p == null) {
            Log.record("Cannot load player " + name);
            System.exit(1);
        }

        return new PlayerWrapper(p, (name + new Random().nextInt(5)));
    }

    // The state that is sent to the GUI. (JSON)
    private static String state(double fps, List<List<Integer>> skills, List<List<List<Integer>>> distribution, Game game) {

        List<String> distrib_a = new ArrayList<String>();
        List<String> distrib_b = new ArrayList<String>();
        for (int i=0; i<3; ++i) {
            distrib_a.add(join(", ", distribution.get(0).get(i)));
            distrib_b.add(join(", ", distribution.get(1).get(i)));
        }

        // Aaaaaaaaaaaaa!!!!
        String json = "{\"refresh\":" + (1000.0/fps) + ",\"is_home\":\"" + isHome +
            "\",\"grp_a\":\"" + player_a.getName() +
            "\",\"grp_b\":\"" + player_b.getName() +
            "\",\"grp_a_skills\":\"" + join(", ", skills.get(0)) +
            "\",\"grp_b_skills\":\"" + join(", ", skills.get(1)) +
            "\",\"grp_a_round\":\"" + join(",", game.player_aRounds.get(game.player_aRounds.size()-1)) +
            "\",\"grp_b_round\":\"" + join(",", game.player_bRounds.get(game.player_bRounds.size()-1)) +
            "\",\"grp_a_dist\":\"" + String.join(";", distrib_a) +
            "\",\"grp_b_dist\":\"" + String.join(";", distrib_b) +
            "\",\"grp_a_score\":\"" + game.player_aScore +
            "\",\"grp_b_score\":\"" + game.player_bScore + "\"}";

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

                        player_a_name = playerNames.get(0);
                        player_b_name = playerNames.get(1);
                    } else if (args[i].equals("-g") || args[i].equals("--gui")) {
                        gui = true;
                    } else if (args[i].equals("-t") || args[i].equals("--tournament")) {
                        tournament = true;
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
        Log.record("Tournament " + (tournament ? "enabled" : "disabled"));

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
