package matchup.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

    	return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {

    	Integer n = selectLine(opponentRound);
	availableRows.remove(n);
	
    	List<Integer> round = distribution.get(n);

	if (opponentRound != null) {
	    round = bestPermutation(round, opponentRound)
	}
       
    	return round;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);
    }
    
    """
    This selects the best list to play for each round and returns its index in availableRows
    """
    public Integer selectLine(List<Integer> opponentRound) {

	"""
        If we are picking first, start with mid range line.
        The idea is to draw out best or worst line againt mid tier players if they just max something,
        then pick randomly (for now)
        """
	if (opponentRound == null) {

	    List<Integer> lineSkill = Arrays.asList(0, 0, 0);

	    for(Integer i : availableRows) {
		lineSkill.set(i, totalLineSkill(distribution.get(i)));
	    }

	    //this should return the mid-tier line or at least an available line 
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
	"""
        If we are picking second, pick line with that wins most (in case of tie, one with fewer skill points)   
        """
	else {
	    //NOTE I think right now this might also be called as away team in rounds 2 and 3 but need to ask in class, not sure if we are passed row from previous round or null

	    List<Integer> lineWins = Arrays.asList(null, null, null);

	    for(Integer i : availableRows) {
		lineWins.set(i, totalLineWins(distribution.get(i), opponentRound));
	    }

	    bestRow = availableRows.get(0);
	    List<Integer> tie = new ArrayList<Integer>();
	    for(Integer i : availableRows) {
		
		if(lineWins.get(i) > lineWins.get(bestRow)) {
		    bestRow = i;
		    tie.clear();
		}
		
		if(lineWins.get(i) == lineWins.get(bestRow) && i != bestRow) {
		    tie.add(bestRow);
		    tie.add(i);
		}
	    }
	    
	    if( tie.size() > 1 ) { //this does not currently consider three way ties
		if(math.Min(totalLineSkill(distribution.get(tie.get(0))), totalLineSkill(distribution.get(tie.get(1)))) == totalLineSkill(distribution.get(tie.get(0)))) {
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

     	for(Integer player : line) {
	    skillLevel += player;
        }

	return skillLevel;
    }

    public Integer totalLineWins(List<Integer> line, List<Integer> opponentLine) {
	line = bestPermutation(line, opponentLine);
       	int rowWins = 0;
       	for(int j=0, j<5, j++){
	    if (line.get(j)-opponentLine.get(j) > 2) rowWins++;
     	    if (line.get(j)-opponentLine.get(j) < -2) rowWins--;
       	}
	return rowWins;
    }

    public List<Integer> bestPermutation(List<Integer> line, List<Integer> opponentLine) {
	return line;
    }
}
