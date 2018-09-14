package matchup.g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Player implements matchup.sim.Player {
	private List<Integer> skills;
	private List<List<Integer>> distribution;

	private List<Integer> availableRows;

	private Random rand;
	
	public Player() {
		rand = new Random();
		skills = new ArrayList<Integer>();
		distribution = new ArrayList<List<Integer>>();
		availableRows = new ArrayList<Integer>();

		for (int i=0; i<3; ++i) availableRows.add(i);
	}
	
    public void init(String opponent) {
    }

    public List<Integer> getSkills() {
	Integer s[] = {6,6,6,6,6,8,8,8,8,8,4,4,4,4,4};
	
	for (int i=0; i<s.length; ++i) {
		skills.add(s[i]);
	}

	return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
	Integer rows[][];

        if (isHome) {
		rows = new Integer[][] {{8,8,6,4,4},{8,8,6,6,4},{8,6,6,4,4}};
	} else {
		rows = new Integer[][] {{6,6,6,6,6},{8,8,8,8,8},{4,4,4,4,4}};
	}
	
	for (int i = 0; i < 3; ++i) {
		distribution.add(new ArrayList<Integer>());
		for (int j = 0; j < 5; ++j) {
			distribution.get(i).add(rows[i][j]);
		}
	}

    	return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
    	//int n = rand.nextInt(availableRows.size());

    	List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(0)));
    	availableRows.remove(0);

    	Collections.shuffle(round);

    	return round;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);

	distribution.clear();
    }
}
