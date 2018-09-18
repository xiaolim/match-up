package matchup.g6;

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

        for (int i = 0; i < 3; ++i) availableRows.add(i);
    }

    public void init(String opponent) {
    }

    public List<Integer> getSkills() {
        // skill distribution modeled after Group 5
        for (int i = 0; i < 3; ++i) {
            skills.add(9); //three 9s
            skills.add(8); //three 8s
            skills.add(1); //three 1s
        }
        for (int i = 0; i < 2; ++i) {
            skills.add(7); //two 7s
            skills.add(6); //two 6s
            skills.add(5); //two 5s
        }

        Collections.shuffle(skills);

        return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {

        List<Integer> index = new ArrayList<Integer>();
        for (int i = 0; i < 15; ++i) index.add(i);

        distribution = new ArrayList<List<Integer>>();

        Collections.shuffle(index);
        int n = 0;
        for (int i = 0; i < 3; ++i) {
            List<Integer> row = new ArrayList<Integer>();
            for (int j = 0; j < 5; ++j) {
                row.add(skills.get(index.get(n)));
                ++n;
            }

            distribution.add(row);
        }

        return distribution;
    }

    public int chooseLine(List<Integer> opponentRound) {
        List<Double> ratio = new ArrayList<Double>();
        for (int i = 0; i < availableRows.size(); i++) {
            List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(i)));
            round = optimizedRound(opponentRound, round);
            double tempScore = 0;
            double tempSkills = 90;
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
            ratio.add(tempScore / tempSkills);
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
    private List<Double> getFrequencyDensity(List<List<Integer>> history) {
        List<Double> result = new ArrayList<>(11);
        for (List<Integer> l : history) {
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

    }
}