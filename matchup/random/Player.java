package matchup.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// To get game history.
import matchup.sim.utils.*;

public class Player implements matchup.sim.Player {
    private List<Integer> skills;
    private List<List<Integer>> distribution;

    private List<Integer> availableRows;

    // Random seed of 42.
    private int seed = 42;
    private Random rand;

    public Player() {
        rand = new Random(seed);
        skills = new ArrayList<Integer>();
        distribution = new ArrayList<List<Integer>>();
        availableRows = new ArrayList<Integer>();

        for (int i=0; i<3; ++i) availableRows.add(i);
    }
    
    public void init(String opponent) {
    }
    public List<Integer> getSkills() {

        //skills.clear();
        skills = new ArrayList<Integer>();
        for (int i=0; i<7; ++i) {
            int x = rand.nextInt(11) + 1;
            skills.add(x);
            skills.add(12 - x);
        }

        skills.add(6);
        Collections.shuffle(skills, rand);

        return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
        List<Integer> index = new ArrayList<Integer>();
        for (int i=0; i<15; ++i) index.add(i);

        distribution = new ArrayList<List<Integer>>();

        Collections.shuffle(index, rand);
        int n = 0;
        for (int i=0; i<3; ++i) {
            List<Integer> row = new ArrayList<Integer>();
            for (int j=0; j<5; ++j) {
                row.add(skills.get(index.get(n)));
                ++n;
            }

            distribution.add(row);
        }

        return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
        int n = rand.nextInt(availableRows.size());

        List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
        availableRows.remove(n);

        Collections.shuffle(round, rand);

        return round;
    }

    public void clear() {
        availableRows.clear();
        for (int i=0; i<3; ++i) availableRows.add(i);

        // Get history of games.
        // List<Game> games = History.getHistory();
        // System.out.println(games.size());
    }
}