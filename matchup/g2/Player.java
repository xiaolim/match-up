package matchup.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;

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

		for (int i = 0; i < 3; ++i)
			availableRows.add(i);
	}

	public void init(String opponent) {
	}

	public List<Integer> getSkills() {
		for (int i = 0; i < 7; ++i) {

			int stdVar = 3;
			int mean = 6;
			int x = (((int) rand.nextGaussian()) * stdVar + mean);

			if (x < 1) {
				x = 1;
			} else if (x > 11) {
				x = 11;
			}
			skills.add(x);
			skills.add(12 - x);
		}

		skills.add(6);
		Collections.shuffle(skills);

		return skills;
	}

	public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
		distribution = new ArrayList<List<Integer>>();

		// List<Integer> skills_L = getSkills().subList(0, 15);
		// System.out.println("skills: " + skills_L);
		// System.out.println("skills: " + skills_L.size());
		List<Integer> skills_L = skills;

		skills_L.sort(null);

		// System.out.println("Sorted skills: " + skills); //

		if (isHome) {
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
			}

			// System.out.println("distributions: " + distribution.get(0) + ", " +
			// distribution.get(1) + ", " + distribution.get(2));

		} else {
			// arrange rows to be optimal for AWAY play
			// System.out.println("AWAY play");

			List<Integer> row1, row2, row3;

			row1 = new ArrayList<Integer>(Arrays.asList(skills_L.get(14), skills_L.get(13), skills_L.get(12),
					skills_L.get(3), skills_L.get(11)));
			row2 = new ArrayList<Integer>(Arrays.asList(skills_L.get(0), skills_L.get(1), skills_L.get(2),
					skills_L.get(4), skills_L.get(10)));
			row3 = new ArrayList<Integer>(
					Arrays.asList(skills_L.get(5), skills_L.get(6), skills_L.get(7), skills_L.get(8), skills_L.get(9)));

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
		line = bestPermutation(line, opponentLine);
       	int rowWins = 0;
       	
       	for(int j=0; j<5; j++){
       		if (line.get(j)-opponentLine.get(j) > 2) rowWins++;
       		if (line.get(j)-opponentLine.get(j) < -2) rowWins--;
       	}
       	return rowWins;
    }

}
