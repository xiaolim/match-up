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
		
		return distribution;
	}
	
	private float findAverage(List<Integer> line) {
		int sum = 0;
		for (int i : line) sum += i;
		return sum / (float)line.size();
	}
	
	private float findVariance(List<Integer> line) {
		// TODO
		float sum = 0;
		float mean = findAverage(line);
		for (int i : line){
			sum += (i - mean)* (i - mean);
		}
		return sum/(line.size()-1.0f);
	}
	
	/**
	 * Find out the optimal permutation of our line against the opponent
	 * @param row The index of our line used
	 * @param opponentRound The list of player skills in the opponent line
	 * @return a pair containing score difference and the optimal permutation
	 */
	private Pair<Integer, List<Integer>> permutation(int row, List<Integer> opponentRound) {
		// TODO Assigned to Warren
		ArrayList<ArrayList<Integer>> all_possible = new ArrayList<ArrayList<Integer>>();
 		
 		all_possible.add(new ArrayList<Integer>());
 		List<Integer> line = distribution.get(row);
		for (int i = 0; i < line.size(); i++) {
			//list of list in current iteration of the array num
			ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();
 
			for (ArrayList<Integer> l : all_possible) {
				// # of locations to insert is largest index + 1
				for (int j = 0; j < l.size()+1; j++) {
					// + add num[i] to different locations
					l.add(j, line.get(i));
 
					ArrayList<Integer> temp = new ArrayList<Integer>(l);
					current.add(temp);
					l.remove(j);
				}
			}
			all_possible = new ArrayList<ArrayList<Integer>>(current);

		}

		int best_score = 0;
		List<Integer> best_lineup = line;
		for (int i=0; i<all_possible.size();i++){
			if (ComputeScore(all_possible.get(i),opponentRound)>best_score){
				best_score = ComputeScore(all_possible.get(i),opponentRound);
				best_lineup = all_possible.get(i);
			}
		}
		return new Pair<Integer, List<Integer>>(best_score,best_lineup); 
	}




	private int ComputeScore(List<Integer> line1, List<Integer> line2){
		int score = 0;
		for (int i=0; i< line1.size(); i++){
			if (line1.get(i) - line2.get(i) >= 3){
				score += 1;
			}
			if (line2.get(i) - line1.get(i) >= 3){
				score -= 1;
			}
		}
		return score;
	}


	protected int useRows(){
		// TODO
		int k = availableRows.get(0);
		availableRows.remove(0);
		return k;
	}

	
	@Override
	public List<Integer> playRound(List<Integer> opponentRound) {
		// TODO Assigned to Will
    	List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(0)));
    	availableRows.remove(0);
		return round;
	}

	@Override
	public void clear() {
		availableRows.clear();
		for (int i = 0; i < 3; i++)
			availableRows.add(i);
	}

}
