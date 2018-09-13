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
	private boolean state;
	public Player() {
		//skills = new ArrayList<Integer>();
		// TODO Find out a good skill set
		skills = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 3, 4, 5, 6, 6, 7, 8, 8, 9, 9, 10, 11));
		availableRows = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
		averageStrength = new ArrayList<Float>();
		state = false;
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
		state = isHome;
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
		for (int i=0; i<distribution.size(); i++){
			averageStrength.add(findAverage(distribution.get(i)));
		}

		return distribution;
	}
	
	private float findAverage(List<Integer> line) {
		int sum = 0;
		for (int i : line) sum += i;
		return sum / (float)line.size();
	}
	
	private float findVariance(List<Integer> line) {
		float sum = 0.0F;
		float mean = findAverage(line);
		for (int i : line){
			sum += i * i;
		}
		return sum - mean * mean / line.size();
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
		int best_score = -6;
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

	protected Pair<Integer, List<Integer>> useRows(List<Integer> opponentRound){
		int best_line = availableRows.get(0);
		int best_score = permutation(availableRows.get(0),opponentRound).getKey();
		List <Integer> line_up = permutation(availableRows.get(0),opponentRound).getValue();
		for (int i=1; i<availableRows.size(); i++){
			int index = availableRows.get(i);
			int cur_score = permutation(index,opponentRound).getKey();
			List <Integer> cur_line_up = permutation(index,opponentRound).getValue();
			if (cur_score > best_score && averageStrength.get(index) > averageStrength.get(best_line)){
				best_line = index;
				best_score = cur_score;
				line_up = cur_line_up;
			}
		}
		//System.out.println(best_score);
		return new Pair<Integer, List<Integer>>(best_line, line_up);
	}

	@Override
	public List<Integer> playRound(List<Integer> opponentRound) {
		// TODO Assigned to Will
		List<Integer> round = new ArrayList<Integer>();

    	if (state){
    		Pair<Integer, List<Integer>> temp = useRows(opponentRound);
    		round = temp.getValue();
    		availableRows.remove(temp.getKey());
    	}
    	else{
	    	round =	distribution.get(availableRows.get(0));
    		availableRows.remove(0);
    	}
		return round;
	}

	@Override
	public void clear() {
		availableRows.clear();
		for (int i = 0; i < 3; i++)
			availableRows.add(i);
	}

}
