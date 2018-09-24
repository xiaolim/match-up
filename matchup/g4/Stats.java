package matchup.g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// To get game history.
import matchup.sim.utils.*;

class Stats {

	private List<Game> games;
	private List<Integer> predOppSkills;
	private boolean isPlayerA;
	private int lossStreak;

	public Stats() {
		games = History.getHistory();
		isPlayerA = games.get(0).playerA.name.equals("g4");

		lossStreak = 0;
	}

	public void update() {
		games = History.getHistory();
        checkLoss();
	}

	private void checkLoss() {
        int lastScoreA = games.get(games.size()-1).playerA.score + games.get(games.size()-2).playerA.score;
        int lastScoreB = games.get(games.size()-1).playerB.score + games.get(games.size()-2).playerB.score;
        int lastScore = lastScoreB - lastScoreA;
        if (isPlayerA) {lastScore = -lastScore;}

        if (lastScore < 0) { //lost last game
            lossStreak ++;
        } else {
            lossStreak = 0; //reset
        }
    }

    public boolean doCounter() {
    	return lossStreak >= 3;
    }

    public Skills getCounter() {
    	lossStreak = 0; // reset, give new strategy a chance to win

        List<List<Integer>> opskills = new ArrayList<List<Integer>>();

    	if (isPlayerA) {
    		for (int i = 5; i > 0; i -= 2) {
    			opskills.add(games.get(games.size()-i).playerB.skills);
    		}
    	} else {
    		for (int i = 5; i > 0; i -= 2) {
    			opskills.add(games.get(games.size()-i).playerA.skills);
    		}
    	}
        

        List<Integer> predOppSkills = new ArrayList<Integer>();

        for (int i=0;i<15;i++) {
            List<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < 3; j++) {
            	temp.add(opskills.get(j).get(i));
            }

            predOppSkills.add(maxmode(temp));
        }

        for (int i = 0; i < 3; i++) {
        	System.out.println(opskills.get(i));
        }
        System.out.println("predicted:");
        System.out.println(predOppSkills);

        return new Skills(counter(predOppSkills));
    }

    private int maxmode(List<Integer> arr) {
        int maxCount = 0;
        int maxKey = 0;
        Collections.sort(arr);
        int curCount = 0;
        int curKey = arr.get(0);
        for (int i=0; i<arr.size();i++) {
            if (arr.get(i) == curKey) { curCount ++;}
            else {
                if (curCount >= maxCount) {
                    maxCount = curCount;
                    maxKey = curKey;
                }
                curCount = 1;
                curKey = arr.get(i);
            }
        }
        if (curCount >= maxCount) {
            maxCount = curCount;
            maxKey = curKey;
        }

        return maxKey;
    }

    private List<Integer> counter(List<Integer> opponentSkills) {
        Collections.sort(opponentSkills);
        for(int i=0; i<opponentSkills.size();i++ ){
            if (i>=6) {
                opponentSkills.set(i, opponentSkills.get(i)-2);
            }
            else if (i<6) {
                opponentSkills.set(i, opponentSkills.get(i)+3);
            }
        }
        Collections.sort(opponentSkills);
        int sum = opponentSkills.stream().mapToInt(Integer::intValue).sum();
        if(sum>90){
            int difference = sum-90;
            int i = opponentSkills.size() - 1;
            while(difference != 0){
                i--;
                if(i<0) i = opponentSkills.size()-1;
                if(opponentSkills.get(i)>=9 && opponentSkills.get(i)>2) continue;
                else{
                	opponentSkills.set(i,opponentSkills.get(i)-1);
                    difference-=1;
                }
            }
        }
        else if (sum<90){
            int difference = 90-sum;
            int i=0;
            while(difference != 0){
                i++;
                if(i==opponentSkills.size()) i = 0;
                if(opponentSkills.get(i)<3 && opponentSkills.get(i)<11) continue;
                else{
                	opponentSkills.set(i,opponentSkills.get(i)+1);
                    difference-=1;
                }
            }
        }
        return opponentSkills;
    }
}