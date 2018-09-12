package matchup.g5;

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

		for (int i=0; i<3; ++i) {
			skills.add(9); //three 9s
			skills.add(8); //three 8s
			skills.add(1); //three 1s
		}
		for (int i=0; i<2; ++i) {
			skills.add(7); //two 7s
			skills.add(6); //two 6s
			skills.add(5); //two 5s
		}

		Collections.shuffle(skills);
		return skills;
	}

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	List<Integer> index = new ArrayList<Integer>();
    	for (int i=0; i<15; ++i) index.add(i);

    	distribution = new ArrayList<List<Integer>>();

		Collections.shuffle(index);
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

    	Collections.shuffle(round);

    	return round;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);
    }
}
