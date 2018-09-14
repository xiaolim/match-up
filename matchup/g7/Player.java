package matchup.g7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.util.Pair;

public class Player implements matchup.sim.Player {
	
	// The list of all player skill levels
	private List<Integer> skills;
	private List<List<Integer>> distribution;
	private List<Float> averageStrength;
	private List<Integer> availableRows;
	
	private List<Integer> opponentRemainSkills;
	
	private boolean state; // Whether the player is playing as the home team
	
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
		this.opponentRemainSkills = new ArrayList<Integer>(opponentSkills);
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
	
	private class PlayRow {
		private List<List<Integer>> opponentRemainDist;
		private int maxScore = -16;
		private int bestLine = -1;
		
		private <E> void swap(List<E> list, int i1, int i2) {
			E temp = list.get(i1);
			list.set(i1, list.get(i2));
			list.set(i2, temp);
		}
		
		private void permuteRow(List<Integer> availableRows, int l, int score) {
			if (l != 0)
	        score += permutation(availableRows.get(l - 1), opponentRemainDist.get(l - 1)).getKey();
			if (l == availableRows.size()) {
				if (score > maxScore) {
					maxScore = score;
					bestLine = availableRows.get(0);
				}
			}
	        else {
	            for (int i = l; i < availableRows.size(); i++) {
	                swap(availableRows, l, i);
	                permuteRow(availableRows, l + 1, availableRows.size()); 
	                swap(availableRows, l, i); 
	            } 
	        } 
	    }
		
		protected Pair<Integer, List<Integer>> useRows(List<Integer> opponentRound){
			// Predict opponent's line distributions
			opponentRemainDist = new ArrayList<List<Integer>>();
			opponentRemainDist.add(opponentRound);
			
			for (int i = 0; i < opponentRemainSkills.size(); i += 5) {
				// Prediction policy
				opponentRemainDist.add(new ArrayList<Integer>(
						opponentRemainSkills.subList(i, i + 5)));
			}
			// Finding the best strategy to counter the prediction
			permuteRow(availableRows, 0, 0);
			
			//System.out.println(best_score);
			return new Pair<Integer, List<Integer>>(bestLine, 
					permutation(bestLine, opponentRound).getValue());
		}
	}
	

	@Override
	public List<Integer> playRound(List<Integer> opponentRound) {
		List<Integer> round = new ArrayList<Integer>();
		
    	if (state){
    		for (Integer i : opponentRound) {
    			opponentRemainSkills.remove(i);
    		}
    		Collections.sort(opponentRemainSkills);
    		Pair<Integer, List<Integer>> temp = new PlayRow().useRows(opponentRound);
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
