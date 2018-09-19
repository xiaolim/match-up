package matchup.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerWrapper {
    private Timer thread;
    private Player player;
    private boolean isHome;
    private String name;

    private List<List<Integer>> distribution;
    private List<Integer> skills;

    private List<Integer> playedRows;

    public PlayerWrapper(Player player, String name) {
        this.player = player;
        this.name = name;
        this.playedRows = new ArrayList<Integer>();
    }

    public void init(String opponent) throws Exception {
        Log.record("Initializing player " + this.name);
        // Initializing ID mapping array
        
        this.player.init(opponent);
    }

    public List<Integer> getSkills() throws Exception {
        Log.record("Getting skills for player " + this.name + " who is " + (this.isHome ? "Home" : "Away"));
        this.skills = this.player.getSkills();

        System.out.println("Player " + this.name + " skills are " + skills.toString());

        int total = 0;
        for (int s : skills) {
            if (s > 11 || s < 1) {
                throw new IllegalArgumentException("Invalid value of a skill " + s + " for player " + this.name);
            }
            total += s;
        }

        if (total != 90) {
            throw new IllegalArgumentException("The total of all skills does not add up to 90 for player "
                + this.name);
        }

        return this.skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) throws Exception {
        Log.record("Getting distribution of skills for player " + this.name);
        distribution = player.getDistribution(opponentSkills, isHome);

        System.out.println("Player " + this.name + " distribution is " + distribution.toString());

        if (distribution.size() != 3 || distribution.get(0).size() != 5) {
            throw new IllegalArgumentException("The dimension of the distribution array is incorrect for player "
                + this.name);
        }

        List<Integer> tempSkills = new ArrayList<Integer>(this.skills);
        for (int i=0; i<3; ++i) {
            for (int j=0; j<5; ++j) {
                tempSkills.remove(new Integer(distribution.get(i).get(j)));
            }
        }

        if (tempSkills.size() != 0) {
            throw new IllegalArgumentException("Player " + this.name + " distribution matrix has a different " +
                "set of skills than originally specified.");
        }

        return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) throws Exception {
        List<Integer> round = player.playRound(opponentRound);

        System.out.println("Player " + this.name + " round is " + round.toString());

        List<Integer> played = new ArrayList<Integer>();
        for (int i=0; i<3; ++i) {
            List<Integer> tempRound = new ArrayList<Integer>(round);
            for (int j=0; j<5; ++j) {
                tempRound.remove(new Integer(distribution.get(i).get(j)));
            }

            if (tempRound.size() == 0) {
                played.add(i);
            }
        }

        if (played.size() == 0) {
            throw new IllegalArgumentException("Player " + this.name + "is playing a round that is not part of " +
                "their distribution.");
        }

        for (int i=0; i<playedRows.size(); ++i) {
            played.remove(new Integer(playedRows.get(i)));
        }

        if (played.size() == 0) {
            throw new IllegalArgumentException("Player " + this.name + "is playing a round that has already been " +
                "played before.");
        }

        playedRows.add(played.get(0));

        return round;
    }

    public void clear() {
        playedRows = new ArrayList<Integer>();
        player.clear();
    }

    public String getName() {
        return name;
    }
}
