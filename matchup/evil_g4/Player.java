package matchup.evil_g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;

// To get game history.
import matchup.sim.utils.*;

public class Player implements matchup.sim.Player {
    private List<Integer> skills;
    private List<List<Integer>> distribution;
    private List<Integer> availableRows;

    private boolean isHome;

    private List<Integer> opponentSkills = new ArrayList<Integer>();
    private List<Integer> opponentSkillsLeft = new ArrayList<Integer>();

    // Random seed of 3.
    private int seed = 3;
    private Random rand;

    public Player() {
        rand = new Random(seed);
        skills = new Skills();
        distribution = new ArrayList<List<Integer>>();
        availableRows = new ArrayList<Integer>();

        for (int i=0; i<3; ++i) availableRows.add(i);
    }
    
    public void init(String opponent) {
    }

    public List<Integer> getSkills() {
		skills.clear();
        for (int i=0; i<7; ++i) {
            int x = rand.nextInt(11) + 1;
            skills.add(x);
            skills.add(12 - x);
        }

        skills.add(6);
        Collections.shuffle(skills, rand);

        return skills;
    }

    // private List<Integer> counter(List<Integer> opponentSkills) {
    //     Collections.sort(opponentSkills);
    //     for(int i=0; i<opponentSkills.size();i++ ){
    //         if(i>=6) opponentSkills.set(i, Integer.valueof(opponentSkills.get(i)-2));
    //         else if(i<6) opponentSkills.set(i, Integer.valueof(opponentSkills.get(i)+3));
    //         return opponentSkills;
    //      }
    // }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	int GROUP_SIZE = 5;
        
        Skills skills = (Skills)this.skills;

        if (isHome) {
        	skills.groupForHome();
        } else {
        	skills.groupForAway();
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
        int idx = 0;

        if (isHome) {
            for (Integer i: opponentRound) {
                opponentSkillsLeft.remove(i);
            }

            if (availableRows.size() == 3) {
                idx = lineToUse(new Line(opponentRound));

            } else if (availableRows.size() == 2) {
                idx = lineToUse2(new Line(opponentRound), new Line(opponentSkillsLeft));   
                
            } else { // size is 1
                Line last = (Line)distribution.get(availableRows.get(0));
                last.permuteFor(new Line(opponentRound));
            }
        } else { // away
            idx = rand.nextInt(availableRows.size());
        }

        toUse = distribution.get(availableRows.get(idx));
        availableRows.remove(idx);

        return toUse;
    }

    public void clear() {
        availableRows.clear();
        for (int i=0; i<3; ++i) availableRows.add(i);

        distribution.clear();

        List<Game> games = History.getHistory();
        //System.out.println(games.size());
        if(games.size() == 6) {
            for (int i=0;i<games.size();i++) {

                Double skillVar = 0.0;
                Double skillMean = 0.0;
                for (Integer n: games.get(i).playerB.skills) {
                    skillMean += n;
                }
                for (Integer n: games.get(i).playerB.skills) {
                    skillVar += Math.pow(n-skillMean,2);
                }
                skillVar /= 4;
                //System.out.println("Skills Var: " + skillVar);

                if (games.get(i).playerB.isHome) {
                    List<Double> homeMeans = new ArrayList<Double>();
                    List<Double> homeVars = new ArrayList<Double>();
                    for(List<Integer> d: games.get(i).playerB.distribution) {
                        Double mean = 0.0;
                        for (Integer n: d) {
                            mean += n;
                        }
                        mean /= 5;
                        homeMeans.add(mean);
                        
                        Double var = 0.0;
                        for (Integer n: d) {
                            var += Math.pow(n-mean,2);
                        }
                        var /= 4;
                        homeVars.add(var);
                    }
                    //System.out.println("Home Dist Means:" + homeMeans);
                    //System.out.println("Home Dist Vars:" + homeVars);

                } else {
                    List<Double> awayMeans = new ArrayList<Double>();
                    List<Double> awayVars = new ArrayList<Double>();
                    for(List<Integer> d: games.get(i).playerB.distribution) {
                        Double mean = 0.0;
                        for (Integer n: d) {
                            mean += n;
                        }
                        mean /= 5;
                        awayMeans.add(mean);

                        Double var = 0.0;
                        for (Integer n: d) {
                            var += Math.pow(n-mean,2);
                        }
                        var /= 4;
                        awayVars.add(var);
                    }
                    //System.out.println("Away Dist Means:" + awayMeans);
                    //System.out.println("Away Dist Vars:" + awayVars);
                }
                //System.out.println(games.get(i).playerA.name);
                //System.out.println(games.get(i).playerA.skills);
                
                //System.out.println(games.get(i).playerB.name);
                //System.out.println(games.get(i).playerB.skills);
                //System.out.println(games.get(i).playerB.rounds);
                //System.out.println(games.get(i).playerB.distribution);
                //System.out.println(games.get(i).playerB.isHome);
                //System.out.println(games.get(i).playerB.score);

            }
        }
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
