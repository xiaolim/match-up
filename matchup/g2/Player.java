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

    private boolean home;
	
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
        
        home = isHome; //store as class member for the playRound function

        distribution = new ArrayList<List<Integer>>();

        skills.sort(null);

        if (isHome) {
            // arrange rows to be optimal for HOME play
            //System.out.println("HOME play"); //

            // Gather information about opponent's skills
            //opp_mean = 6
            //opp_stdv = 2.47

            opponentSkills.sort(null);
            System.out.println("sorted skills: " + opponentSkills); //
            int opp_range = opponentSkills.get(14) - opponentSkills.get(0);
            System.out.println("range: " + opp_range); //

            // If RANGE is HIGH or LOW do something different

            Map<Integer, Integer> opp_skill_count = new HashMap<Integer, Integer>();
            for (int s : opponentSkills) {
                if (!opp_skill_count.containsKey(s)) {
                    opp_skill_count.put(s, 1);
                } else {
                    int s_inc = opp_skill_count.get(s);
                    opp_skill_count.replace(s, ++s_inc);
                }
            }
            System.out.println("skill count: " + opp_skill_count);


            List<Integer> leftover = new ArrayList<Integer>();

            for (int i=0; i<3; ++i) {
                List<Integer> row = new ArrayList<Integer>();
                List<Integer> indices = new ArrayList<Integer>(Arrays.asList(i, i+3, i+6, (14-(i+3)), (14-i)));
                //System.out.println("row " + i + ": " + indices + " (indices)"); //

                for (int ix : indices) {
                    if (!row.contains(skills.get(ix))) row.add(skills.get(ix));
                    else leftover.add(skills.get(ix));
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

            row1 = new ArrayList<Integer>(Arrays.asList(skills.get(14), skills.get(13), skills.get(12), skills.get(3), skills.get(11)));
            row2 = new ArrayList<Integer>(Arrays.asList(skills.get(0), skills.get(1), skills.get(2), skills.get(4), skills.get(10)));
            row3 = new ArrayList<Integer>(Arrays.asList(skills.get(5), skills.get(6), skills.get(7), skills.get(8), skills.get(9)));

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
