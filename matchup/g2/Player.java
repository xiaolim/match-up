package matchup.g2;

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
            
            int stdVar = 3;
            int mean = 6;
            int x = (((int) rand.nextGaussian()) * stdVar  + mean );

            if( x < 1 ){
                x = 1;
            }
            else if (x > 11){
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


        //System.out.println("Sorted skills: " + skills); //

        if (isHome) {
            // arrange rows to be optimal for HOME play
            //System.out.println("HOME play"); //

            List<Integer> leftover = new ArrayList<Integer>();

            for (int i=0; i<3; ++i) {
                List<Integer> row = new ArrayList<Integer>();
                List<Integer> indices = new ArrayList<Integer>(Arrays.asList(i, i+3, i+6, (14-(i+3)), (14-i)));
                //System.out.println("row " + i + ": " + indices + " (indices)"); //

                for (int ix : indices) {
                    if (!row.contains(skills_L.get(ix))) row.add(skills_L.get(ix));
                    else leftover.add(skills_L.get(ix));
                }

                //System.out.println("row " + i + ": " + row + " (values)");
                distribution.add(row);
            }

            
            //System.out.println("skills leftover: " + leftover);
            //System.out.println("distributions: " + distribution.get(0) + ", " + distribution.get(1) + ", " + distribution.get(2));

            for (int s : leftover) {
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

            row1 = new ArrayList<Integer>(Arrays.asList(skills_L.get(14), skills_L.get(13), skills_L.get(12), skills_L.get(3), skills_L.get(11)));
            row2 = new ArrayList<Integer>(Arrays.asList(skills_L.get(0), skills_L.get(1), skills_L.get(2), skills_L.get(4), skills_L.get(10)));
            row3 = new ArrayList<Integer>(Arrays.asList(skills_L.get(5), skills_L.get(6), skills_L.get(7), skills_L.get(8), skills_L.get(9)));

            distribution.add(row1);
            distribution.add(row2);
            distribution.add(row3);
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
