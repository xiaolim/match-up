package matchup.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

// To get game history.
import matchup.sim.utils.*;

public class Player implements matchup.sim.Player {
	private List<Integer> skills;
	private List<List<Integer>> distribution;

	private List<Integer> availableRows;

	private Random rand;

	private boolean home;

	private List<Integer> bestLine = new ArrayList<Integer>();
	private int score; 
	private int counter; 

	private Map<Integer, Double> historySkillCount;
	private Map<Integer, Long> historySkillPercents;
	private Map<String, List<Double>> historySkillStats;
	private Map<String, Double> aveSkillHistory;
	private Map<String, Map<Integer, List<Double>>> historyLineStats;
	private Map<String, Map<Integer, Double>> aveLineHistory;
	private Map<Integer, List<Integer>> popularSkills;

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

	// -- Gather information about opponent's skills --
	private Map<String, Double> getSkillStats(List<Integer> skills) {

		Map<String, Double> stats = new HashMap<String, Double>();

		double sum = 0.0;
		double mean = 0.0;
		for (int i : skills) {
			sum += i;
		}
		mean = sum/skills.size();
		stats.put("mean", mean);

		skills.sort(null);

		// Min and max
		double min, max;
		min = skills.get(0);
		max = skills.get(skills.size()-1);
		stats.put("min", min);
		stats.put("max", max);

		// Range
		double range = max - min;
		stats.put("range", range);

		// Opponent standard deviation
		double stdev;
		double sqr_sum = 0;
		for (int s : skills) {
			sqr_sum += Math.pow((s-6), 2);
		}
		stdev = Math.sqrt(sqr_sum/(skills.size()-1));
		stats.put("stdev", stdev);

		return stats;
	}

	// -- Get a count of all skills present --
	private Map<Integer, Integer> getSkillCount(List<Integer> skills) {

		Map<Integer, Integer> skillCount = new HashMap<Integer, Integer>();
		for (int s : skills) {
			if (!skillCount.containsKey(s))
				skillCount.put(s, 1);
			else
				skillCount.replace(s, skillCount.get(s)+1);
		}

		return skillCount;
	}

	
	// -- Map one set of skills to another set of skills that according to win, tie, lose outcome
	private Map<String, Map<Integer, List<Integer>>> getSkillMapping(List<Integer> baseSkills, List<Integer> oppSkills) {
		/*
		* Both skill parameters need to be collections of unique skills e.g. pass in opp_skill_count.keySet()
		* baseSkills: these are the skills that will be they keys of the map
		* oppSkills: these are the skills that will be the values mapped to keys of the map
		*/

		Map<Integer, List<Integer>> win = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> tie = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> lose = new HashMap<Integer, List<Integer>>();

		for (int base_s : baseSkills) {
			
			List<Integer> val_win = new ArrayList<Integer>();
			List<Integer> val_tie = new ArrayList<Integer>();
			List<Integer> val_lose = new ArrayList<Integer>();
			
			for (int opp_s : oppSkills) {
				if (base_s - opp_s >= 3) {
					val_win.add(opp_s);
				} else if (Math.abs(base_s - opp_s) <=2) {
					val_tie.add(opp_s);
				} else if (base_s - opp_s <= -3) {
					val_lose.add(opp_s);
				}
			}

			win.put(base_s, val_win);
			tie.put(base_s, val_tie);
			lose.put(base_s, val_lose);

		}

		Map<String, Map<Integer, List<Integer>>> mapping = new HashMap<String, Map<Integer, List<Integer>>>();
		mapping.put("wins_against", win);
		mapping.put("ties_against", tie);
		mapping.put("loses_against", lose);

		return mapping;

	
	}

	// NINE 9s one 4 five 1s
	public List<Integer> getSkills() {

		// ###############################################
		// ### Gather information about previous games ###
		// ###############################################

		// Get history of games
		//-> this is the first function called once a round has started so we can collect history info in this function
		//-> store the information in class members for other functions to use
	        List<Game> games = History.getHistory();
	        double numGamePairs = games.size();
	        //System.out.println(games.size());

	        PlayerData opponent;
	        List<Integer> oppSkills;
	        Map<String, Double> oppSkillStats;
	        Map<Integer, Integer> oppSkillCount;

	        List<String> statsLong = new ArrayList<String>(Arrays.asList("stdev", "mean", "min", "max", "range"));
	        List<String> statsShort = new ArrayList<String>(Arrays.asList("stdev", "min", "max"));
	        
	        // historySkillCount keeps track of the number of times each skill have shown up across all previous games
	        historySkillCount = new HashMap<Integer, Double>();
	        for (int i=1; i<12; i++) {
	        	historySkillCount.put(i, 0.0);
	        }

	        // historySkillPercents keeps track of the frequency of each skill across all previous games
	        historySkillPercents = new HashMap<Integer, Long>();

	        // historySkillStats keeps track of the statistics for the overall skill distributions in each previous game
	        historySkillStats = new HashMap<String, List<Double>>();
	        for (String s : statsShort) {
	        	historySkillStats.put(s, new ArrayList<Double>());
	        }

	        // aveSkillHistory keeps track of the average statistics for the overall skill distribution across all previous games
	        aveSkillHistory = new HashMap<String, Double>();
	        for (String s : statsShort) {
	        	aveSkillHistory.put(s, 0.0);
	        }

	        // historyLineStats keeps track of the statistics for each of the opponent's lines
	        historyLineStats = new HashMap<String, Map<Integer, List<Double>>>();
	        //the lines are ordered according to the order in which they are played in the game
	        for (String s : statsLong) {
	        	historyLineStats.put(s, new HashMap<Integer, List<Double>>());
	        	for (int i=0; i<3; i++) {
	        		historyLineStats.get(s).put(i, new ArrayList<Double>());
	        	}	
	        }

	        // aveLineHistory keeps track of the average statistics for each of the opponent's lines across all previous games
	       	aveLineHistory = new HashMap<String, Map<Integer, Double>>();
	        for (String s : statsLong) {
	        	aveLineHistory.put(s, new HashMap<Integer, Double>());
	        }

	        // popularSkills contains a single list of skills in order of decreasing frequency of use in the opponent's skill distribution
	        popularSkills = new LinkedHashMap<Integer, List<Integer>>();


	        boolean notNull = false;
	        for (Game g : games) {

	        	notNull = false;
	        	// Find out which player is the opponent
	        	if (g.playerA.name.equals("g2")) {
	        		opponent = g.playerB;
	        		oppSkills = g.playerB.skills; 
	        	} else {
	        		opponent = g.playerA;
	        		oppSkills = g.playerA.skills;
	        	}

	        	if (!oppSkills.isEmpty()) {
	        		notNull = true;
				
				oppSkillStats = getSkillStats(oppSkills);
				oppSkillCount = getSkillCount(oppSkills);

	        		// Update the total skill count for the skills in the overall distribution
	        		for (int s : oppSkillCount.keySet()) {
	        			double count_current = historySkillCount.get(s);
	        			count_current += oppSkillCount.get(s);
	        			historySkillCount.replace(s, count_current);
	        		}
	        		//System.out.println("historySkillCount: " + historySkillCount); //

	        		// Add the statistics of the overall skill distribution for this round to historySkillStats
	        		for (String stat : historySkillStats.keySet()) {
	        			historySkillStats.get(stat).add(oppSkillStats.get(stat));
	        		}
	        		//System.out.println("historySkillStats: " + historySkillStats); //

	        		// For each line, add the statistics of the line skill distribution for this round to historyLineStats
	        		for (int i=0; i<3; i++) {
	        			List<Integer> line = opponent.rounds.get(i);
	        			Map<String, Double> lineStats= getSkillStats(line);
	        			for (String stat : historyLineStats.keySet()) {
	        				historyLineStats.get(stat).get(i).add(lineStats.get(stat));
	        			}
	        		}
	        		//System.out.println("historyLineStats: " + historyLineStats); //
        		}

	        }

	        Long maxSkillUsed = 0L;
	        int maxSkill = 0;
	        
	        if (notNull) {
		        
		        //System.out.println("historySkillCount: " + historySkillCount); //

		        // Get percentages for the skill counts in the overall skill distribution
		        for (int s : historySkillCount.keySet()) {
		        	//System.out.println(historySkillCount.get(s));
		        	double percent = (historySkillCount.get(s)/(numGamePairs*15.0))*100.0;
		        	historySkillPercents.put(s, Math.round(percent));
		        }
		        //System.out.println("historySkillPercents: " + historySkillPercents); //

		        // Get averages for the overall skill distribution statistics		
			for (String stat : historySkillStats.keySet()) {
				double total = 0.0;
				for (int val=0; val<historySkillStats.get(stat).size(); val++) {
					total += val;
				}
				aveSkillHistory.replace(stat, total/historySkillStats.get(stat).size());
			}
			//System.out.println("aveSkillHistory: " + aveSkillHistory); //

			// Get averages for the line statistics
			for (String stat : historyLineStats.keySet()) {
				for (int i=0; i<3; i++) {
					double total = 0.0;
					for (int val=0; val<historyLineStats.get(stat).get(i).size(); val++) {
						total += val;
					}
					aveLineHistory.get(stat).put(i, total/historyLineStats.get(stat).get(i).size());
				}
			}
			//System.out.println("aveLineHistory: " + aveLineHistory); //

		        // Create a list of skills in order of decreasing frequency of use in the opponents skill distribution
		        // Each pair consists of a count and a list of all skills with that count (to catch the case in which there more than one skill is used the most)
			List<Long> sortedVals = new ArrayList(historySkillPercents.values());
			sortedVals.sort(null);
			Collections.reverse(sortedVals);
			List<Integer> usedVals = new ArrayList<Integer>();
			for (Long longVal : sortedVals) {
				int val = longVal.intValue();
				if (!usedVals.contains(val)) {
					List<Integer> sList = new ArrayList<Integer>();
					for (int s : historySkillPercents.keySet()) {
						if (historySkillPercents.get(s).intValue() == val)
							sList.add(s);
					}
					popularSkills.put(val, sList);
					usedVals.add(val);
				}
			}
			//System.out.println("popularSkills: " + popularSkills); //
			 
		}

		// ##########################################################
		// ### End collection of information about previous games ###
		// ##########################################################

		maxSkill = 0;

		int pickRandLine;

		if (maxSkill == 7) {
			pickRandLine = 0;
		} else if (maxSkill == 9) {
			pickRandLine = 1;
		} else {
			pickRandLine = rand.nextInt(2);
		}



		skills = new ArrayList<Integer>();


		if (pickRandLine == 1){
			skills.add(4); // adding one 4
			for (int i = 0 ; i < 9; i++){

				//adding nine 9s
				skills.add(9);

				//adding five 1s
				if(i%2 == 0){
					skills.add(1);
				}
			}
		}
		else {
			for (int i = 0 ; i < 10;i++){

	            //adding nine 7s
	            skills.add(7);

	            //adding five 4s
	            if(i%2 == 0)
	                skills.add(4);
        		}
		}
		return skills;
	}

	public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {

		distribution = new ArrayList<List<Integer>>();

		skills.sort(null);
		//System.out.println("our skills: " + skills);
		//System.out.println("opponent skills: " + opponentSkills);

		if (isHome) {
			// -- Arrange rows to be optimal for HOME play --
		
			opponentSkills.sort(null);
			// get stats on our skills
			Map<String, Double> ourStats = getSkillStats(skills);
			//System.out.println("ourStats: " + ourStats);

			// get our skill count
			Map<Integer, Integer> selfSkillCount = getSkillCount(skills);
			//System.out.println("selfSkillCount: " + selfSkillCount);

			// get stats on opponent's skills
			Map<String, Double> oppStats = getSkillStats(opponentSkills);
			//System.out.println("oppStats: " + oppStats);

			// get opponent's skill count
			Map<Integer, Integer> oppSkillCount = getSkillCount(opponentSkills);
			//System.out.println("oppSkillCount: " + oppSkillCount);

			// get our skill mapping: for each of our skills find out which of the opponent's skills it will beat, tie to, lose against
			Map<String, Map<Integer, List<Integer>>> selfSkillMapping = getSkillMapping(skills, opponentSkills);
			//System.out.println("selfSkillMapping: " + selfSkillMapping);

			// get opponent's sill mapping: for each of opponent's skills find out which of our skills it will beat, tie to, lose against
			Map<String, Map<Integer, List<Integer>>> oppSkillMapping = getSkillMapping(opponentSkills, skills);
			//System.out.println("oppSkillMapping: " + oppSkillMapping);


			/*// >> Split lines differently depending on opponent skill count
			if (oppSkillCount.values().equals(Arrays.asList(5, 5, 5))) {
				System.out.println("! opponent has three values repeated 5 times each!");
			} else if (Collections.max(oppSkillCount.values()) > 6) {
				System.out.println("opponent has one value repeated over 7 times");
			} else {
				System.out.println("no specific opponent skill distribution");
			}*/

			// get the skills that win against at least 1 thing (but then this can be leveraged with counts)
			Map<Integer, List<Integer>> win_skills = new HashMap<Integer, List<Integer>>();
			int win_skills_count = 0; // how many skills (incl repetitions) we have that win against at least 1 of opp skills
			List<Integer> wins = new ArrayList<Integer>();
			for (Integer self_s : selfSkillMapping.get("wins_against").keySet()) {
				List<Integer> counts = new ArrayList<Integer>(); // count of how many we have, count of how may opp skills it wins against
				if (selfSkillMapping.get("wins_against").get(self_s).size() > 0) {
					counts.add(selfSkillCount.get(self_s));
					win_skills_count += selfSkillCount.get(self_s);
					counts.add(selfSkillMapping.get("wins_against").get(self_s).size());
					if (!wins.contains(selfSkillMapping.get("wins_against").get(self_s).size()))
						wins.add(selfSkillMapping.get("wins_against").get(self_s).size());
					win_skills.put(self_s, counts);
				}
			}
			wins.sort(null);
			Collections.reverse(wins);
			//System.out.println("win_skills: " + win_skills);
			//System.out.println("win_skills_count: " + win_skills_count);
			//System.out.println("wins: " + wins);

			// -- Distribute win skills into 3 lines --
			for (int c=0; c<3; c++)
				distribution.add(new ArrayList<Integer>());

			for (int win_count : wins) {
				for (int win_skill : win_skills.keySet()) {
					if (win_skills.get(win_skill).get(1) == win_count) {
						int i=0;
						int added=0;
						while (added != win_skills.get(win_skill).get(0)) {
							if (distribution.get(i%3).size() < 5) {
								distribution.get(i%3).add(win_skill);
								i++;
								added++;
							} else {
								i++;
							}
						}
					}
				}
			}
			//System.out.println("distribution after distributing win_skills: " + distribution);


			// get the skills that tie against at least 1 thing (but then this can be leveraged with counts)
			// first we need to remove any of the win skills we've already distributed
			for (int win_skill : win_skills.keySet())
				selfSkillMapping.get("ties_against").remove(win_skill);
			//System.out.println("new ties_against: " + selfSkillMapping.get("ties_against"));
			
			Map<Integer, List<Integer>> tie_skills = new HashMap<Integer, List<Integer>>();
			if (!selfSkillMapping.get("ties_against").isEmpty()) {
				int tie_skills_count = 0; // how many skills (incl repetitions) we have that tie against at least 1 of opp skills
				List<Integer> ties = new ArrayList<Integer>();
				for (Integer self_s : selfSkillMapping.get("ties_against").keySet()) {
					List<Integer> counts = new ArrayList<Integer>(); // count of how many we have, count of how may opp skills it ties against
					if (selfSkillMapping.get("ties_against").get(self_s).size() > 0) {
						counts.add(selfSkillCount.get(self_s));
						tie_skills_count += selfSkillCount.get(self_s);
						counts.add(selfSkillMapping.get("ties_against").get(self_s).size());
						if (!ties.contains(selfSkillMapping.get("ties_against").get(self_s).size()))
							ties.add(selfSkillMapping.get("ties_against").get(self_s).size());
						tie_skills.put(self_s, counts);
					}
				}
				ties.sort(null);
				Collections.reverse(ties);
				//System.out.println("tie_skills: " + tie_skills);
				//System.out.println("tie_skills_count: " + tie_skills_count);
				//System.out.println("ties: " + ties);

				// -- Distribute tie skills into 3 lines --
				for (int tie_count : ties) {
					//System.out.println("tie count: " + tie_count);
					for (int tie_skill : tie_skills.keySet()) {
						if (tie_skills.get(tie_skill).get(1) == tie_count) {
							//System.out.println("tie skill with matching count: " + tie_skill);
							int i=0;
							int added=0;
							while (added != tie_skills.get(tie_skill).get(0)) {
								if (distribution.get(i%3).size() < 5) {
									distribution.get(i%3).add(tie_skill);
									i++;
									added++;
								} else {
									i++;
								}
							}
						}
					}
				}
				//System.out.println("distribution after distributing tie_skills: " + distribution);
			}

			// get the skills that lose against at least 1 thing (but then this can be leveraged with counts)
			// first we need to remove any of the win and tie skills we've already distributed
			for (int win_skill : win_skills.keySet())
				selfSkillMapping.get("loses_against").remove(win_skill);
			for (int tie_skill : tie_skills.keySet())
				selfSkillMapping.get("loses_against").remove(tie_skill);
			//System.out.println("new loses_against: " + selfSkillMapping.get("loses_against"));
			
			Map<Integer, List<Integer>> lose_skills = new HashMap<Integer, List<Integer>>();
			if (!selfSkillMapping.get("loses_against").isEmpty()) {
				int lose_skills_count = 0; // how many skills (incl repetitions) we have that lose against at least 1 of opp skills
				List<Integer> losses = new ArrayList<Integer>();
				for (Integer self_s : selfSkillMapping.get("loses_against").keySet()) {
					List<Integer> counts = new ArrayList<Integer>(); // count of how many we have, count of how may opp skills it loses against
					if (selfSkillMapping.get("loses_against").get(self_s).size() > 0) {
						counts.add(selfSkillCount.get(self_s));
						lose_skills_count += selfSkillCount.get(self_s);
						counts.add(selfSkillMapping.get("loses_against").get(self_s).size());
						if (!losses.contains(selfSkillMapping.get("loses_against").get(self_s).size()))
							losses.add(selfSkillMapping.get("loses_against").get(self_s).size());
						lose_skills.put(self_s, counts);
					}
				}
				losses.sort(null);
				Collections.reverse(losses);
				//System.out.println("lose_skills: " + lose_skills);
				//System.out.println("lose_skills_count: " + lose_skills_count);
				//System.out.println("losses: " + losses);

				// -- Distribute tie skills into 3 lines --
				for (int lose_count : losses) {
					//System.out.println("lose count: " + lose_count);
					for (int lose_skill : lose_skills.keySet()) {
						if (lose_skills.get(lose_skill).get(1) == lose_count) {
							//System.out.println("lose skill with matching count: " + lose_skill);
							int i=0;
							int added=0;
							while (added != lose_skills.get(lose_skill).get(0)) {
								if (distribution.get(i%3).size() < 5) {
									distribution.get(i%3).add(lose_skill);
									i++;
									added++;
								} else {
									i++;
								}
							}
						}
					}
				}
				//System.out.println("distribution after distributing lose_skills: " + distribution);
			}

			
			

		/*if (isHome) {
			// arrange rows to be optimal for HOME play
			// System.out.println("HOME play"); //

			List<Integer> leftover = new ArrayList<Integer>();

			for (int i = 0; i < 3; ++i) {
				List<Integer> row = new ArrayList<Integer>();
				List<Integer> indices = new ArrayList<Integer>(
						Arrays.asList(i, i + 3, i + 6, (14 - (i + 3)), (14 - i)));
				// System.out.println("row " + i + ": " + indices + " (indices)"); //

				for (int ix : indices) {
					if (!row.contains(skills_L.get(ix)))
						row.add(skills_L.get(ix));
					else
						leftover.add(skills_L.get(ix));
				}

				// System.out.println("row " + i + ": " + row + " (values)");
				distribution.add(row);
			}

			// System.out.println("skills leftover: " + leftover);
			// System.out.println("distributions: " + distribution.get(0) + ", " +
			// distribution.get(1) + ", " + distribution.get(2));

			for (int s : leftover) {
				boolean added = false;
				for (int i = 0; i < 3; ++i) {
					if ((distribution.get(i).size() < 5) && !(distribution.get(i).contains(s))) {
						distribution.get(i).add(s);
						added = true;
					} else {
						continue;
					}
				}
				if (!added) {
					for (int i = 0; i < 3; ++i) {
						if (distribution.get(i).size() < 5)
							distribution.get(i).add(s);
					}
				}
			}*/

			// System.out.println("distributions: " + distribution.get(0) + ", " +
			// distribution.get(1) + ", " + distribution.get(2));

		} else {
			// arrange rows to be optimal for AWAY play
			// System.out.println("AWAY play");

			List<Integer> row1, row2, row3;

			row1 = new ArrayList<Integer>(Arrays.asList(skills.get(14), skills.get(13), skills.get(12), skills.get(3), skills.get(11)));
			row2 = new ArrayList<Integer>(Arrays.asList(skills.get(0), skills.get(1), skills.get(2), skills.get(4), skills.get(10)));
			row3 = new ArrayList<Integer>(Arrays.asList(skills.get(5), skills.get(6), skills.get(7), skills.get(8), skills.get(9)));

			distribution.add(row1);
			distribution.add(row2);
			distribution.add(row3);
		}

		// System.out.println("distributions: " + distribution.get(0) + ", " +
		// distribution.get(1) + ", " + distribution.get(2));

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

