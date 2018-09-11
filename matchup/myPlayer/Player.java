package matchup.myPlayer;

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
		// for (int i=0; i<7; ++i) {
		// 	int x = rand.nextInt(11) + 1;
		// 	skills.add(x);
		// 	skills.add(12 - x);
		// }

		// skills.add(6);
		// Collections.shuffle(skills);

		List<Integer> skills = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			skills.add(9);
		}

		for (int i = 0; i < 3; i++) {
			skills.add(7);
		}

		for (int i = 0; i < 3; i++) {
			skills.add(5);
		}

		for (int i = 0; i < 4; i++) {
			skills.add(4);
		}

		for (int i = 0; i < 1; i++) {
			skills.add(2);
		}

		return skills;
	}

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	List<Integer> row1 = new ArrayList<Integer>();
    	row1.add(9);
    	row1.add(9);
    	row1.add(5);
    	row1.add(5);
    	row1.add(4);

    	List<Integer> row2 = new ArrayList<Integer>();
    	row2.add(9);
    	row2.add(7);
    	row2.add(7);
    	row2.add(4);
    	row2.add(2);

    	List<Integer> row3 = new ArrayList<Integer>();
    	row3.add(9);
    	row3.add(7);
    	row3.add(5);
    	row3.add(4);
    	row3.add(4);

    	distribution = new ArrayList<List<Integer>>();

    	distribution.add(row1);
    	distribution.add(row2);
    	distribution.add(row3);

  //   	List<Integer> index = new ArrayList<Integer>();
  //   	for (int i=0; i<15; ++i) index.add(i);

  //   	distribution = new ArrayList<List<Integer>>();

		// Collections.shuffle(index);
		// int n = 0;
  //   	for (int i=0; i<3; ++i) {
  //   		List<Integer> row = new ArrayList<Integer>();
  //   		for (int j=0; j<5; ++j) {
  //   			row.add(skills.get(index.get(n)));
  //   			++n;
  //   		}

  //   		distribution.add(row);
  //   	}

    	return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
    	List<Integer> round = null;
    	if (opponentRound == null) {
	    	int min_sum = 60;
	    	int min_row = -1;

	    	for (int i = 0; i < availableRows.size(); i++){
	    		int sum = 0;
	    		for(int j: distribution.get(i)) {
	    			sum += j;
				}

				if (sum < min_sum){
					min_sum = sum;
					min_row = i;
				}
	    	}

	    	round = distribution.get(min_row);
	    	availableRows.remove(min_row);

    	}
    	return round;
    	// int n = rand.nextInt(availableRows.size());

    	// List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
    	// availableRows.remove(n);

    	// Collections.shuffle(round);

    	// return round;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);
    }
}
