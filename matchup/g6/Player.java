package matchup.g6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.*;
import javafx.util.Pair;
import matchup.sim.utils.*;

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

        for (int i = 0; i < 3; ++i) availableRows.add(i);
    }

    public void init(String opponent) {
    }

    public List<Integer> getSkills() {
        // reset skills
        skills.clear();

        // distribution with low standard deviation
        // for (int i = 0; i < 15; i++) {
        //     Random generator = new Random();
        //     double num = generator.nextGaussian();
        //     System.out.println("gaus: "+num);
        //     int mean = 6;
        //     int stdDev = 3;
        //     double x = stdDev * num + mean;

        //     if (x < 1) {
        //         x = 1;
        //     } else if (x > 11) {
        //         x = 9;
        //     } else if (skills.size() == 14) {
        //         x = leftover;
        //     }

        //     int skillpt = (int) Math.round(x);

        //     leftover = leftover - skillpt;
        //     System.out.println("leftover: "+leftover);

        //     skills.add(skillpt);
        // }

        List<Game> games = History.getHistory();
        int sz = games.size();

        List<Integer> opponentPastSkills = new ArrayList<Integer>();
        int range = Integer.MIN_VALUE;

        if (sz > 1) {
            // get opponent past skill distribution
            // System.out.print(games.get(sz - 1).playerB.name + ": ");
            // System.out.println(games.get(sz - 1).playerB.skills);
            opponentPastSkills = games.get(sz - 1).playerB.skills;
            Collections.sort(opponentPastSkills);
            opponentPastSkills.get(0);
            range = opponentPastSkills.get(14) - opponentPastSkills.get(0);
        }

        // if range of opponent skill level is 4-8
        if (range > 3 && range < 9) {
            for (int i = 0; i < 10; i++) {
                skills.add(7);
            }

            for (int i = 0; i < 5; i++) {
                skills.add(4);
            }
        }

        // if range is 0
        else if (range == 0) {
            for (int i = 0; i < 5; i++) {
                skills.add(9);
            }
            for (int i = 0; i < 5; i++) {
                skills.add(5);
            }
            for (int i = 0; i < 5; i++) {
                skills.add(4);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                skills.add(1);
            }

            for (int i = 0; i < 5; i++) {
                skills.add(8);
            }

            for (int i = 0; i < 5; i++) {
                skills.add(9);
            }
        }

        int verifySum = 0;
        /*** James new learning version skills generator ***/
        //getFrequencyDensity();
        if (History.getHistory()!=null && History.getHistory().size() > 3 ) {
            skills.clear();
            List<Integer> counterStrats = Arrays.asList(4, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            List<Double> density = getFrequencyDensity();
            skills = new ArrayList<>(Collections.nCopies(15, 1));
            int remainingSkill = 75;
            List<Pair<Integer, Integer>> myFreqEst = new ArrayList<>();
            int i = 1;
            for (double x : density) {
                myFreqEst.add(new Pair<>(i++, Math.round((float) x * 15)));
            }

            Collections.sort(myFreqEst, Comparator.comparingInt(Pair::getValue));
            int skillIndex = 0;
            int densityIndex = 0;
            while (remainingSkill > 0 && skillIndex < 11) {
                Pair<Integer, Integer> p = myFreqEst.get(densityIndex);
                for (int j = 0; j < p.getValue(); j++) {
                    if (counterStrats.get(p.getKey()-1) > skills.get(skillIndex)) {
                        remainingSkill -= counterStrats.get(p.getKey()-1) - skills.get(skillIndex);
                        skills.set(skillIndex++, counterStrats.get(p.getKey()-1));
                    }
                }
                densityIndex++;
            }
            skillIndex=14;
            while(remainingSkill > 0){
                if(skills.get(skillIndex)+1<12) {
                    remainingSkill--;
                    skills.set(skillIndex--, skills.get(skillIndex + 1) + 1);
                }
                else skillIndex--;
                if(skillIndex<8)skillIndex=14;
            }
            skillIndex=14;
            while(remainingSkill > 0){
                if(skills.get(skillIndex)+1<12) {
                    remainingSkill--;
                    skills.set(skillIndex--, skills.get(skillIndex + 1) + 1);
                }
                else skillIndex--;
                if(skillIndex<0)skillIndex=14;
            }


            for(int x: skills){
                verifySum+=x;
            }

        }

        return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {

        //get density list
        //System.out.println("Density dist");
        //printListDouble(getFrequencyDensity());

        List<Integer> index = new ArrayList<Integer>();
        for (int i = 0; i < 15; ++i) index.add(i);
        List<Integer> skill_copy = new ArrayList<Integer>(skills);
        Collections.sort(skill_copy, Collections.reverseOrder());

        distribution = new ArrayList<List<Integer>>();

        // int n = 0;
        // for (int i = 0; i < 3; ++i) {
        //     List<Integer> row = new ArrayList<Integer>();
        //     for (int j = 0; j < 5; ++j) {
        //         row.add(skills.get(index.get(n)));
        //         ++n;
        //     }

        //     distribution.add(row);
        // }
        List<Integer> line1 = new ArrayList<Integer>();
        List<Integer> line2 = new ArrayList<Integer>();
        List<Integer> line3 = new ArrayList<Integer>();
        distribution.add(line1);
        distribution.add(line2);
        distribution.add(line3);
        List<Integer> sum = new ArrayList<Integer>();
        sum.add(0);
        sum.add(0);
        sum.add(0);
        if (!isHome) {
            for (int i = 0; i < skill_copy.size(); i++) {
                int skill = skill_copy.get(i);
                int min = Collections.min(sum);
                int indexM = sum.indexOf(min);
                distribution.get(indexM).add(skill);
                sum.set(indexM, min + skill);
                if (distribution.get(indexM).size() >= 5) {
                    sum.set(indexM, 90);
                }
            }
        } else {
            int count = 0;
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 5; k++) {
                    int skill = skill_copy.get(count);
                    distribution.get(j).add(skill);
                    count++;
                }
            }

        }

        return distribution;
    }

    public int chooseLine(List<Integer> opponentRound) {
        List<Double> ratio = new ArrayList<Double>();
        List<Double> score = new ArrayList<Double>();
        List<Double> skills = new ArrayList<Double>();

        for (int i = 0; i < availableRows.size(); i++) {
            List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(i)));
            round = optimizedRound(opponentRound, round);
            double tempScore = 0;
            double tempSkills = 0;
            for (int j = 0; j < 5; j++) {
                if (round.get(j) - opponentRound.get(j) > 2) {
                    tempScore += 1;
                } else if (opponentRound.get(j) - round.get(j) > 2) {
                    tempScore -= 1;
                }
                tempSkills += round.get(j);
                tempSkills -= opponentRound.get(j);
            }
            //System.out.println(tempScore/tempSkills);
            score.add(tempScore);
            skills.add(tempSkills);
        }
        double minSkill = Collections.min(skills);
        double maxSkill = Collections.max(skills);
        double minScore = Collections.min(score);
        double maxScore = Collections.max(score);
        for (int i = 0; i < availableRows.size(); i++) {
            double scaledScore = (1 + score.get(i) - minScore) / (availableRows.size() + maxScore - minScore);
            double scaledSkill = (1 + skills.get(i) - minSkill) / (availableRows.size() + maxSkill - minSkill);
            ratio.add(scaledScore / scaledSkill);
        }
        double max = Collections.max(ratio);
        int index = ratio.indexOf(max);
        //System.out.println(index);
        return index;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
        int n = 0;
        if (opponentRound != null) {
            n = chooseLine(opponentRound);
        } else {
            n = rand.nextInt(availableRows.size());
        }

        List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
        availableRows.remove(n);

        Collections.shuffle(round);

        /*** addition to Random ***/
        // If home, Run optimized permutation search given opponentRound instead of using random shuffle
        if (opponentRound != null) {
            round = optimizedRound(opponentRound, round);
        }

        return round;
    }

    public void clear() {
        availableRows.clear();
        for (int i = 0; i < 3; ++i) availableRows.add(i);
        distribution.clear();

        List<Game> previousGames = History.getHistory();

        // Get history of games.
        // List<Game> games = History.getHistory();
        // System.out.println("game no: " + games.size());

        // int sz = games.size();

        // for (int i = 0; i < sz; i++) {
        //     System.out.print(games.get(i).playerA.name + ": ");
        //     System.out.println(games.get(i).playerA.distribution);
        //     System.out.print(games.get(i).playerB.name + ": ");
        //     System.out.println(games.get(i).playerB.distribution);
        // } 
    }


    /*** custom helper functions ***/
    private void printList(List<Integer> myList) {
        System.out.print("[");
        for (int i = 0; i < myList.size() - 1; i++) {
            System.out.print(myList.get(i) + ", ");
        }
        System.out.print(myList.get(myList.size() - 1));
        System.out.println("]");
    }

    private void printListDouble(List<Double> myList) {
        System.out.print("[");
        for (int i = 0; i < myList.size() - 1; i++) {
            System.out.print(myList.get(i) + ", ");
        }
        System.out.print(myList.get(myList.size() - 1));
        System.out.println("]");
    }

    private void print2DList(List<List<Integer>> my2DList) {
        System.out.print("[\n");
        for (int i = 0; i < my2DList.size() - 1; i++) {
            System.out.print("  ");
            printList(my2DList.get(i));
        }
        System.out.print("  ");
        printList(my2DList.get(my2DList.size() - 1));
        System.out.println("\n]");
    }

    private void permute(List<Integer> myList, int l, int r, List<List<Integer>> result) {
        if (l == r) {
            result.add(new ArrayList<Integer>(myList));
        } else {
            for (int i = l; i < r; i++) {
                Collections.swap(myList, l, i);
                permute(myList, l + 1, r, result);
                Collections.swap(myList, l, i);
            }
        }
    }

    private List<List<Integer>> getPermutation(List<Integer> myList) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (myList.isEmpty()) return result;
        permute(myList, 0, myList.size(), result);
        return result;
    }

    private int compareGain(List<Integer> myRound, List<Integer> opponentRound) {
        int gain = 0;
        for (int i = 0; i < myRound.size(); i++) {
            int diff = myRound.get(i) - opponentRound.get(i);
            if (diff >= 3) {
                gain++;
            } else if (diff <= -3) {
                gain--;
            }
        }
        return gain;
    }

    private List<Integer> optimizedRound(List<Integer> opponentRound, List<Integer> myRound) {
        //assert opponentRound size = 5
        if (opponentRound.size() != 5) return myRound;

        //brute-force permutation
        List<List<Integer>> permutations = getPermutation(myRound);

        int maxGain = Integer.MIN_VALUE;
        int maxIndex = -1;

        for (int i = 0; i < permutations.size(); i++) {
            int gain = compareGain(permutations.get(i), opponentRound);
            if (gain > maxGain) {
                maxGain = gain;
                maxIndex = i;
            }
        }
        return permutations.get(maxIndex);
    }

    /*** learning strategy helper ***/

    //return 1-11 skill frequency density from history
    private List<Double> getFrequencyDensity() {
        List<List<Integer>> myHistory = new ArrayList<>();
        for (Game g : History.getHistory()) {
            List<Integer> tmp = new ArrayList<>();
            PlayerData opponent;
            if(g.playerA.name.compareTo("g6") == 0){
                opponent=g.playerB;
            }
            else {
                opponent=g.playerA;
            }
            for (int x : opponent.skills) {
                tmp.add(x);
            }
            myHistory.add(tmp);
        }

        List<Double> result = new ArrayList<>(Collections.nCopies(11, 0.0));
        for (List<Integer> l : myHistory) {
            for (int x : l) {
                result.set(x - 1, result.get(x - 1) + 1);
            }
        }
        double sum = 0;
        for (double x : result) {
            sum += x;
        }
        //std norm
        for (int i = 0; i < result.size(); i++) {
            result.set(i, result.get(i) / sum);
        }
        return result;
    }

    //return 1-11 skill frequency density from history - stream in
    private static List<Double> frequencyDensity;
    private static int historyCount = 0;

    private void updateFrequencyDensity(List<Integer> streamInDistribution) {
        if (frequencyDensity == null) {
            frequencyDensity = new ArrayList<>(11);
            double sum = 0;
            for (double x : streamInDistribution) {
                sum += x;
            }
            //std norm
            for (int i = 0; i < streamInDistribution.size(); i++) {
                frequencyDensity.set(i, streamInDistribution.get(i) / sum);
            }
            historyCount = 1;
        } else {
            List<Integer> tmpFreq = new ArrayList<>(11);
            for (int x : streamInDistribution) {
                tmpFreq.set(x - 1, tmpFreq.get(x - 1) + 1);
            }
            //update
            for (int i = 0; i < frequencyDensity.size(); i++) {
                frequencyDensity.set(i, frequencyDensity.get(i) / (historyCount + 1) * historyCount + streamInDistribution.get(i) / (++historyCount * 15));
            }
        }
    }

    //return a counter strategy distribution given frequencyDensity
    //greedy search: high freq counter first, low tie first then high win
    private List<Integer> generateCounterDistribution(List<Double> frequencyDensity) {
        int skills = 90;
        List<Integer> result = new ArrayList<>(15);
        return result;

    }
}