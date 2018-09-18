package matchup.g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public class Player implements matchup.sim.Player {
    private List<Integer> skills;
    private List<List<Integer>> distribution;
    private List<Integer> availableRows;

    private boolean isHome;

    private List<Integer> opponentSkills = new ArrayList<Integer>();
    private List<Integer> opponentSkillsLeft = new ArrayList<Integer>();
    
    public Player() {
        skills = new ArrayList<Integer>();
        distribution = new ArrayList<List<Integer>>();
        availableRows = new ArrayList<Integer>();

        for (int i=0; i<3; ++i) availableRows.add(i);
    }
    
    public void init(String opponent) {
    }

    public List<Integer> getSkills() {
		Integer s[] = {9,9,9,9,9,8,8,8,8,8,1,1,1,1,1};
		this.skills = new Skills(Arrays.asList(s));

		return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	int GROUP_SIZE = 5;
        
        Skills skills = (Skills)this.skills;

        if (isHome) {
        	skills.groupForHome(GROUP_SIZE);
        } else {
        	skills.groupForAway(GROUP_SIZE);
        }

        for (int i = 0; i < skills.size(); i += GROUP_SIZE) {
        	distribution.add(new Line(skills.subList(i, i+GROUP_SIZE)));
        }

        this.isHome = isHome;

        for (Integer i: opponentSkills) {
            this.opponentSkills.add(i);
            this.opponentSkillsLeft.add(i);
        }
        return distribution;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {  
        List<Integer> toUse;
        if (isHome) {
            for (Integer i: opponentRound) {
                    opponentSkillsLeft.remove(i);
            }

            if (availableRows.size() == 3) {
                int idx = lineToUse(new Line(opponentRound));
                toUse = distribution.get(availableRows.get(idx));
                availableRows.remove(idx);
            }
            
            else if (availableRows.size() == 2) {
                int idx = lineToUse2(new Line(opponentRound), new Line(opponentSkillsLeft));
                toUse = distribution.get(availableRows.get(idx));
                availableRows.remove(idx);
                
            } else { // size is 1
                Line last = new Line(distribution.get(availableRows.get(0)));
                last.permuteFor(new Line(opponentRound));
                availableRows.remove(0);
                toUse = (List<Integer>) last;
            } 

        }   else { // away
            toUse = distribution.get(availableRows.get(0));
            availableRows.remove(0);
        }

        return toUse;
    }

    public void clear() {
        availableRows.clear();
        for (int i=0; i<3; ++i) availableRows.add(i);

        distribution.clear();
    }

    private int lineToUse(Line opponent) {
        int record = 0;
        double highScore = Double.MIN_VALUE;

        for (int i = 0; i < availableRows.size(); ++i) {
            Line curLine = (Line)distribution.get(availableRows.get(i));
            curLine.permuteFor(opponent);
            double score = curLine.scoreWeighted(opponent);

            if (score > highScore) {
                record = i;
                highScore = score;
            }
        }

        return record;
    }

    private int lineToUse2(Line opponent1, Line opponent2) {
    	Line temp1 = (Line)distribution.get(availableRows.get(0));
    	Line temp2 = (Line)distribution.get(availableRows.get(1));

        temp1.permuteFor(opponent1);
    	Line temp3 = new Line(temp1);
    	temp2.permuteFor(opponent2);
    	int score1 = temp1.scoreAgainst(opponent1) + temp2.scoreAgainst(opponent2);

        temp1.permuteFor(opponent2);
        temp2.permuteFor(opponent1);
        int score2 = temp1.scoreAgainst(opponent2) + temp2.scoreAgainst(opponent1);

    	temp1 = temp3;

        return (score2 > score1) ? 1 : 0;
    }
}
