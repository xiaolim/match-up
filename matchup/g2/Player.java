package matchup.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.util.Map;
import java.util.HashMap;

public class Player implements matchup.sim.Player {
	private List<Integer> skills; 
	private List<List<Integer>> distribution;

	private List<Integer> availableRows;

	private Random rand;

	private List<Integer> bestLine = new ArrayList<Integer>();
	private int score; 
	private int counter; 

	public Player() {
		rand = new Random();
		skills = new ArrayList<Integer>();
		distribution = new ArrayList<List<Integer>>();
		availableRows = new ArrayList<Integer>();

		for (int i = 0; i < 3; ++i)
			availableRows.add(i);
	}

	public void init(String opponent) {
	}

	// public List<Integer> getSkills() {
	// 	for (int i = 0; i < 7; ++i) {

	// 		int stdVar = 3;
	// 		int mean = 6;
	// 		int x = (((int) rand.nextGaussian()) * stdVar + mean);

	// 		if (x < 1) {
	// 			x = 1;
	// 		} else if (x > 11) {
	// 			x = 11;
	// 		}
	// 		skills.add(x);
	// 		skills.add(12 - x);
	// 	}

	// 	skills.add(6);
	// 	Collections.shuffle(skills);

	// 	return skills;
	// }

	// NINE 9s one 4 five 1s
	public List<Integer> getSkills() {
		
		skills.add(4); // adding one 4
		for (int i = 0 ; i < 9; i++){

			//adding nine 9s
			skills.add(9);

			//adding five 1s
			if(i%2 == 0){
				skills.add(1);
			}
		}
		return skills;
	}

	public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
		
		distribution = new ArrayList<List<Integer>>();

		skills.sort(null);

		//System.out.println(skills); //

		if (isHome) {
			// -- Arrange rows to be optimal for HOME play --

			// -- Gather information about opponent's skills --
			// Opponent mean: 6

			opponentSkills.sort(null);

			// Opponent range:
			int opp_range = opponentSkills.get(14) - opponentSkills.get(0);
			System.out.println("opp_range: " + opp_range);

			// If range is HIGH or LOW do something different <<<

			// Opponent standard deviation:
			double opp_stdev;
			double sqr_sum = 0;
			for (int s : opponentSkills) {
				sqr_sum += Math.pow((s-6), 2);
			}
			opp_stdev = Math.sqrt(sqr_sum/14);
			System.out.println("opp_stdev: " + opp_stdev);

			// Opponent skill count:
			Map<Integer, Integer> opp_skill_count = new HashMap<Integer, Integer>();
			/*for (int s : opponentSkills) {
				if (!opp_skill_count.containsKey(s))
					opp_skill_count.put(s, 1);
				else
					opp_skill_count.replace(s, opp_skill_count.get(s)+1);
			}
			*/
			opp_skill_count.put(4, 5);
			opp_skill_count.put(6, 8);
			opp_skill_count.put(8, 5);
			System.out.println("opp_skill_count: " + opp_skill_count);

			// >> Split lines differently depending on opponent skill count
			if (opp_skill_count.values().equals(Arrays.asList(5, 5, 5))) {
				System.out.println("! opponent has three values repeated 5 times each!");
			} else if (Collections.max(opp_skill_count.values()) > 7) {
				System.out.println("opponent has one value repeated over 7 times");
			} else {
				System.out.println("no specific opponent skill distribution");
			}

			
			// -- Distribute skills into 3 lines --
			List<Integer> leftover = new ArrayList<Integer>();

			for (int i=0; i<3; i++) {
				List<Integer> row = new ArrayList<Integer>();
				List<Integer> indices = new ArrayList<Integer>(Arrays.asList(i, (i+3), (i+6), (14 - (i + 3)), (14 - i)));
				//System.out.println("row " + i + ": " + indices + " (indices)"); //

				for (int ix : indices) {
					int skill = skills.get(ix);
					if (!row.contains(skill))
						row.add(skill);
					else
						leftover.add(skill);
				}

				//System.out.println("row " + i + ": " + row + " (values)"); //
				distribution.add(row);
			}
			leftover.sort(null);
			//System.out.println("leftover: " + leftover); //

			boolean added;
			for (int c=0; c<leftover.size(); c++) {
				int s = leftover.get(c);
				//System.out.println(c + ", " + s);
				added = false;
				for (int i=0; i<3; i++) {
					//System.out.println(distribution.get(i));
					if ((distribution.get(i).size() < 5) && (!distribution.get(i).contains(s))) {
						distribution.get(i).add(s);
						added = true;
						//System.out.println("added " + s + " >> " + distribution.get(i));
						break;
					}
				}
				if (!added) {
					for (int j=0; j<3; j++) {
						if (distribution.get(j).size() < 5) {
							distribution.get(j).add(s);
							added = true;
							//System.out.println("added " + s + " to row " + j + " >> " + distribution.get(j));
							break;
						}
					}
				}
			}
		} else {
			// arrange rows to be optimal for AWAY play

			List<Integer> row1, row2, row3;

			row1 = new ArrayList<Integer>(Arrays.asList(skills.get(14), skills.get(13), skills.get(12), skills.get(3), skills.get(11)));
			row2 = new ArrayList<Integer>(Arrays.asList(skills.get(0), skills.get(1), skills.get(2), skills.get(4), skills.get(10)));
			row3 = new ArrayList<Integer>(Arrays.asList(skills.get(5), skills.get(6), skills.get(7), skills.get(8), skills.get(9)));

			distribution.add(row1);
			distribution.add(row2);
			distribution.add(row3);
		}

		// System.out.println("distributions: " + distribution.get(0) + ", " +
		// distribution.get(1) + ", " + distribution.get(2));*/

		return distribution;
	}

	public List<Integer> playRound(List<Integer> opponentRound) {

		Integer n = selectLine(opponentRound);
		availableRows.remove(n);

		List<Integer> round = distribution.get(n);

        //TODO: make permutation work here 
		if (opponentRound != null) {
			round = bestPermutation(round, opponentRound);
		}

		return round;
	}

	public void clear() {
		availableRows.clear();
		for (int i = 0; i < 3; ++i)
			availableRows.add(i);
	}

	// This selects the best list to play for each round and returns its index in
	// availableRows

	public Integer selectLine(List<Integer> opponentRound) {

		/*
		 * If we are picking first, start with mid range line. The idea is to draw out
		 * best or worst line againt mid tier players if they just max something, then
		 * pick randomly (for now)
		 */

		if (opponentRound == null) {

			List<Integer> lineSkill = Arrays.asList(0, 0, 0);

			for (Integer i : availableRows) {
				lineSkill.set(i, totalLineSkill(distribution.get(i)));
			}

			// this should return the mid-tier line or at least an available line
			int zeroSkill = lineSkill.get(0);
			int maxSkill = Math.max(lineSkill.get(1), lineSkill.get(2));

			if (zeroSkill == 0) {
				return lineSkill.indexOf(maxSkill);
			} else {
				if (maxSkill == 0) {
					return lineSkill.indexOf(zeroSkill);
				} else {
					return lineSkill.indexOf(Math.min(zeroSkill, maxSkill));
				}
			}
		}
		/*
		 * If we are picking second, pick line with that wins most (in case of tie, one
		 * with fewer skill points)
		 */
		else {
			// NOTE I think right now this might also be called as away team in rounds 2 and
			// 3 but need to ask in class, not sure if we are passed row from previous round
			// or null

			List<Integer> lineWins = Arrays.asList(null, null, null);

			for (Integer i : availableRows) {
				lineWins.set(i, totalLineWins(distribution.get(i), opponentRound));
			}

			Integer bestRow = availableRows.get(0);
			List<Integer> tie = new ArrayList<Integer>();
			for (Integer i : availableRows) {

				if (lineWins.get(i) > lineWins.get(bestRow)) {
					bestRow = i;
					tie.clear();
				}

				if (lineWins.get(i) == lineWins.get(bestRow) && i != bestRow) {
					tie.add(bestRow);
					tie.add(i);
				}
			}

			if (tie.size() > 1) { // this does not currently consider three way ties
				if (Math.min(totalLineSkill(distribution.get(tie.get(0))),
						totalLineSkill(distribution.get(tie.get(1)))) == totalLineSkill(distribution.get(tie.get(0)))) {
					return tie.get(0);
				} else {
					return tie.get(1);
				}
			} else {
				return bestRow;
			}
		}
	}

	public Integer totalLineSkill(List<Integer> line) {
		int skillLevel = 0;

		for (Integer player : line) {
			skillLevel += player;
		}

		return skillLevel;
	}

	public Integer totalLineWins(List<Integer> line, List<Integer> opponentLine) {
		line = bestPermutation(line,  opponentLine);
       	int rowWins = 0;
       	
       	for(int j=0; j<5; j++){
       		if (line.get(j)-opponentLine.get(j) > 2) rowWins++;
       		if (line.get(j)-opponentLine.get(j) < -2) rowWins--;
       	}
       	return rowWins;
    }

    public void permute(List<Integer> line, int j, List<Integer> opponentLine){ 
        for(int i = j; i < line.size(); i++){
            java.util.Collections.swap(line, i, j);
            permute(line, j+1, opponentLine); 
            java.util.Collections.swap(line, j, i);
        }

        if(j == line.size() -1){
            counter++; 
            //System.out.println(counter + java.util.Arrays.toString(line.toArray())); 
            int temp = compareLine(line, opponentLine); 

            if(temp > score){ 
                score = temp; 
                //System.out.println("I just set the score: " + score); 
                bestLine.clear(); 
                bestLine.addAll(line); 
                //System.out.println("I just set best line: " + bestLine); 
            }
        }
    }

   //System.out.println("This is the best line end of permute: " + bestLine); 
 

    //figure out which of two lines win 
    public int compareLine(List<Integer> home, List<Integer> away){ 
        int homeScore = 0; 

        //System.out.println("I'm in compare line!"); 
        for(int i=0; i<5; i++){ 
            if(home.get(i) - away.get(i) >= 3){
                homeScore ++; 
            }
            if(away.get(i) - home.get(i) >= 3){ 
                homeScore --; 
            }
        }
        return homeScore; 
    }

    

    private List<Integer> bestPermutation(List<Integer> home, List<Integer> away){ 
        bestLine.clear(); 
        score = -100; 
        counter = 0; 

        permute(home, 0, away); 

        return bestLine; 

    }
}
