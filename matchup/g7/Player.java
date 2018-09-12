package matchup.g7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

public class Player implements matchup.sim.Player {
	
	// The list of all players, should be finalized before game starts
	private final List<Integer> skills;
	private List<List<Integer>> distribution;
	private List<Float> averageStrength;
	private List<Integer> availableRows;

	public Player() {
		//skills = new ArrayList<Integer>();
		// TODO Find out a good skill set
		skills = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 3, 4, 5, 6, 6, 7, 8, 8, 9, 9, 10, 11));
		availableRows = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
	}
	
	@Override
	public void init(String opponent) {
	}

	@Override
	public List<Integer> getSkills() {
		return skills;
	}

	@Override
	public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
		// TODO Come up with a way to form distribution against opponentSkills and the team position
		distribution = new ArrayList<List<Integer>>();
		if (isHome) {
			distribution.add(new ArrayList<Integer>(Arrays.asList(11, 10, 8, 6, 4)));
			distribution.add(new ArrayList<Integer>(Arrays.asList(9, 7, 5, 3, 1)));
			distribution.add(new ArrayList<Integer>(Arrays.asList(9, 8, 6, 2, 1)));
		}
		else {
			distribution.add(new ArrayList<Integer>(Arrays.asList(11, 10, 9, 9, 7)));
			distribution.add(new ArrayList<Integer>(Arrays.asList(8, 8, 6, 5, 4)));
			distribution.add(new ArrayList<Integer>(Arrays.asList(6, 3, 2, 1, 1)));
		}
		
		// Calculating the average of player skills in each line
		for (List<Integer> line : distribution)
			averageStrength.add(findAverage(line));
		
		return distribution;
	}
	
	private float findAverage(List<Integer> line) {
		int sum = 0;
		for (int i : line) sum += i;
		return sum / 5.0F;
	}
	
	private float findVariance(List<Integer> line) {
		// TODO
	}
	
	/**
	 * Find out the optimal permutation of our line against the opponent
	 * @param row The index of our line used
	 * @param opponentRound The list of player skills in the opponent line
	 * @return a pair containing score difference and the optimal permutation
	 */
	private Pair<Integer, List<Integer>> permutation(int row, List<Integer> opponentRound) {
		// TODO Assigned to Warren
		return new Pair<Integer, List<Integer>>(0, distribution.get(row));
	}
	
	protected int useRows(){
		// TODO
		n = availableRows.get(0);
		availableRows.remove(0);
		return n;
	}

	
	@Override
	public List<Integer> playRound(List<Integer> opponentRound) {
		// TODO Assigned to Will
		return distribution.get(useRows());
	}

	@Override
	public void clear() {
		availableRows.clear();
		for (int i = 0; i < 3; i++)
			availableRows.add(i);
	}

}
