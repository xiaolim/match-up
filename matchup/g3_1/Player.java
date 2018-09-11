package matchup.g3_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    	if(isHome) {
        	distribution = new ArrayList<List<Integer>>();
    		Collections.sort(skills);
        	for (int i=0; i<3; ++i) {
        		List<Integer> row = new ArrayList<Integer>();
        		for (int j=0; j<5; ++j) {
        			row.add(skills.get(i*5+j));
        		}

        		distribution.add(row);
        		System.out.println(distribution);
        	}
    	}else {
    	List<Integer> index = new ArrayList<Integer>();
    	for (int i=0; i<15; ++i) index.add(i);

    	distribution = new ArrayList<List<Integer>>();

		Collections.shuffle(index);
		int n = 0;
    	for (int i=0; i<3; ++i) {
    		List<Integer> row = new ArrayList<Integer>();
    		for (int j=0; j<5; ++j) {
    			row.add(skills.get(index.get(n)));
    			++n;
    		}

    		distribution.add(row);
    	  }
    	}
    	return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
    	
//     int n = rand.nextInt(availableRows.size());
//     List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
//     availableRows.remove(n);
     if(opponentRound == null) {
            int n = rand.nextInt(availableRows.size());
            List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
            availableRows.remove(n);
        	Collections.shuffle(round);
        	return round;
     }
     int skills = 0, score = 0, ratio = 0, index = 0;
     List<Integer> result = null;
     for(int n=0;n<availableRows.size();n++) {
    	List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
        List<List<Integer>> res= new ArrayList<>();
        List<Integer> tempList = new ArrayList<>(); 
        backtrace(res, tempList, round, 0);   
        if(n==0) result = res.get(0);
        for(int i=0;i<result.size();i++) {
        	if(result.get(i)-opponentRound.get(i)>=3) score++;
        	if(result.get(i)-opponentRound.get(i)<=-3) score--;
        	skills = skills+result.get(i);
        }
        ratio = score/skills;
        for(List<Integer> cur: res) {
            for(int i=0;i<cur.size();i++) {
            	if(cur.get(i)-opponentRound.get(i)>=3) score++;
            	if(cur.get(i)-opponentRound.get(i)<=-3) score--;
            	skills = skills+cur.get(i);
            }
            if(score/skills>ratio) {
            	ratio = score/skills;
            	result = cur;
            	index = n;
            }
        }
     }
     availableRows.remove(index);
     return result;
    }
    private void backtrace(List<List<Integer>> result, List<Integer> tempList, List<Integer> round, int start) {
    	if(start==5) {
    		result.add(new ArrayList<>(tempList));
    		return;
    	} 
    	Integer temp=0;
    	for(int i=0;i<round.size();i++) {
    		temp = round.get(0);
    		tempList.add(temp);
    	    round.remove(round.indexOf(temp));
    	  	backtrace(result, tempList, round , start+1); 
    		tempList.remove(tempList.size()-1);
    		round.add(temp);
    	}
  
    }
    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);
    }
}
