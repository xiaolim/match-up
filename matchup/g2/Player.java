package matchup.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

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
		for (int i=0; i<7; ++i) {
			int x = rand.nextInt(11) + 1;
			skills.add(x);
			skills.add(12 - x);
		}

		skills.add(6);
		Collections.shuffle(skills);

		return skills;
	}

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
        distribution = new ArrayList<List<Integer>>();

        skills.sort(null);
        //System.out.println("Sorted skills: " + skills); //

        if (isHome) {
            // arrange rows to be optimal for HOME play
            //System.out.println("HOME play"); //

            List<Integer> skills_leftover = new ArrayList<Integer>();

            for (int i=0; i<3; ++i) {
                List<Integer> row = new ArrayList<Integer>();
                List<Integer> indices = new ArrayList<Integer>(Arrays.asList(i, i+3, i+6, (14-(i+3)), (14-i)));
                System.out.println("row " + i + ": " + indices + " (indices)"); //

                for (int ix : indices) {
                    if (!row.contains(skills.get(ix))) row.add(skills.get(ix));
                    else skills_leftover.add(skills.get(ix));
                }

                //System.out.println("row " + i + ": " + row + " (values)");
                distribution.add(row);
            }

            
            //System.out.println("skills leftover: " + skills_leftover);
            //System.out.println("distributions: " + distribution.get(0) + ", " + distribution.get(1) + ", " + distribution.get(2));

            for (int s : skills_leftover) {
                boolean added = false;
                for (int i=0; i<3; ++i) {
                    if ((distribution.get(i).size() < 5) && !(distribution.get(i).contains(s))) {
                        distribution.get(i).add(s);
                        added = true;
                    } else {
                        continue;
                    }
                }
                if (!added) {
                    for (int i=0; i<3; ++i) {if (distribution.get(i).size() < 5) distribution.get(i).add(s);}
                }
            }

            //System.out.println("distributions: " + distribution.get(0) + ", " + distribution.get(1) + ", " + distribution.get(2));



        } else {
            // arrange rows to be optimal for AWAY play
            //System.out.println("AWAY play");

            List<Integer> row1, row2, row3;

            row1 = new ArrayList<Integer>(Arrays.asList(skills.get(14), skills.get(13), skills.get(12), skills.get(3), skills.get(11)));
            row2 = new ArrayList<Integer>(Arrays.asList(skills.get(0), skills.get(1), skills.get(2), skills.get(4), skills.get(10)));
            row3 = new ArrayList<Integer>(Arrays.asList(skills.get(5), skills.get(6), skills.get(7), skills.get(8), skills.get(9)));

            distribution.addAll(row1, row2, row3);
        }

        //System.out.println("distributions: " + distribution.get(0) + ", " + distribution.get(1) + ", " + distribution.get(2));

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
