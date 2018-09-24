package matchup.g7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import javafx.util.Pair;


public class Player implements matchup.sim.Player {
    
    // The list of all player skill levels
    private List<Integer> skills;
    private List<List<Integer>> distribution;
    private List<Float> averageStrength;
    private List<Integer> availableRows;
    private List<Integer> opponentRemainSkills;
    // keep track of history distribution
    private Map<Integer, Integer> dic;
    // keep track of their home line up
    private List<Float> home_line;
    private float opponentVar = 0;
    // keep track of their away line up
    private List<List<Integer>> away_line;


    
    private boolean state; // Whether the player is playing as the home team
    
    public Player() {
        // TODO Find out a good skill set
        skills = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 4, 9, 9, 9, 9, 9, 9, 9, 9, 9));
        availableRows = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
        averageStrength = new ArrayList<Float>();
        state = false;
        dic = new HashMap<Integer, Integer>();
        for (int i = 1; i < 12; i++){
            dic.put(i, 1);
        }    
    }
    
    @Override
    public void init(String opponent) {
        skills = stat();
        home_line = new ArrayList<Float>();
        away_line = new ArrayList<List<Integer>>();
    }
    
    
    @Override
    public List<Integer> getSkills() {
        return skills;
    }
    
    @Override
    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
        // TODO Come up with a way to form distribution against opponentSkills and the team position
        state = isHome;
        distribution = new ArrayList<List<Integer>>();
        this.opponentRemainSkills = new ArrayList<Integer>(opponentSkills);
        opponentVar = findVariance(opponentSkills);

        if (isHome) {
            distribution = counter_lineup(away_line);
        }
        else {
            distribution = counter_var(home_line);
        }

        
        for (int i=0; i<distribution.size(); i++){
            averageStrength.add(findAverage(distribution.get(i)));
        }
        
        for (int i = 0 ; i < opponentSkills.size(); i++){
            dic.put(opponentSkills.get(i), dic.get(opponentSkills.get(i)) + 1);
        }
        
        return new ArrayList<List<Integer>>(distribution);
    }
    
    public int sum(List<Integer> list) {
        int sum = 0;
        
        for (int i : list)
            sum = sum + i;
        
        return sum;
    }
    
    
    
    public List<List<Integer>> counter_var(List<Float> line){
        float sum = 0;
        for (Float i : line)
            sum = sum + i;
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (sum > 0){
            Collections.sort(skills);
            for (int i=0 ; i< 3; i++){
                List<Integer> temp = new ArrayList<Integer>();
                for (int j=0; j<5; j++){
                    temp.add(skills.get(i*5+j));
                }
                result.add(temp);
            }
        }
        else{
            Collections.sort(skills);
            for (int i=0 ; i< 3; i++){
                List<Integer> temp = new ArrayList<Integer>();
                for (int j=0; j<5; j++){
                    temp.add(skills.get(i+j*3));
                }
                result.add(temp);
            }


        }
        return result;
    }
    
    /**
     * Compute the optimal counter lineup to counter against the opponent based on history
     * @param line a ArrayList contains their past line up history
     * @return a line up that counters their lineup directly
     */
    public List<List<Integer>> counter_lineup(List<List<Integer>> line){
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> team = new ArrayList<Integer>(skills);
        
        if (line.size() == 0){
            Random rand = new Random();
            for (int i=0 ; i< 3; i++){
                List<Integer> temp = new ArrayList<Integer>();
                for (int j=0; j< 5; j++){
                    int n = rand.nextInt(team.size());
                    temp.add(team.get(n));
                    team.remove(n);
                }
                result.add(temp);
            }
        }
        
        for (int i=0; i<line.size(); i++){
            List<Integer> temp = new ArrayList<Integer>();
            for (int j=0; j<line.get(i).size(); j++){
                int opponent_score = line.get(i).get(j);
                temp.add(best_counter(opponent_score, team));
                team.remove(best_counter(opponent_score,team));
            }
            result.add(temp);
        }
        return result;
    }
    
    
    private int best_counter(int skill, List<Integer> team){
        // best strategy: win by 3
        if (team.contains(skill+3)){
            return skill+3;
        }
        // second best strategy: tie by -1 or -2
        else if (min_tie_score(skill, team) != 0){
            return min_tie_score(skill, team);
        }
        
        // third best strategy: win by just good enough
        else if (min_winning_score(skill, team)!= 0){
            if (min_winning_score(skill, team) - skill < skill - Collections.min(team)){
                return min_winning_score(skill, team);
            }
        }
        // strategy: lose by a lot
        else{
            return Collections.min(team);
        }
        return team.get(0);
    }
    
    /**
     * compute the minimum score that wins against opponent skill
     * @param skill an opponent skill to be countered
     * @param team our skill distribution
     * @return a minimum score that wins against opponent skill
     */
    private int min_winning_score(int skill, List<Integer> team){
        int min = 0;
        
        if (Collections.max(team) - skill >= 3){
            min = Collections.max(team);
        }
        else{
            return 0;
        }
        for (int i=0; i<team.size(); i++){
            if (team.get(i) - skill >= 3){
                if (team.get(i) < min){
                    min = team.get(i);
                }
            }
        }
        return min;
    }
    
    
    /**
     * compute the minimum score that ties against opponent skill
     * @param skill an opponent skill to be countered
     * @param team our skill distribution
     * @return a minimum score that ties against opponent skill
     */
    private int min_tie_score(int skill, List<Integer> team){
        int min = 0;
        for (int i=0; i<team.size(); i++){
            if (team.get(i) <= skill && skill - team.get(i) < 3){
                if (team.get(i) < min){
                    min = team.get(i);
                }
            }
        }
        return min;
    }
    
    
    /**
     * Compute the optimal distribution to counter against the opponent based on dic
     * @return a list containing optimal distribution
     */
    private List<Integer> stat(){
        Map<Integer, Double> best_dist = new HashMap<Integer,Double>();
        List<Integer> result = new ArrayList<Integer>();
        for (int i=0 ; i< 12; i++){
            best_dist.put(i, 0.0);
        }
        
        for (Map.Entry<Integer, Integer> entry : dic.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
        }
        double sum = 0;
        for (Map.Entry<Integer, Integer> entry : dic.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            
            if (value > 0){
                if (key - 2 > 0 && key + 3 < 12){
                    best_dist.put(key-2, best_dist.get(key-2) + (double)value * 3/5);
                    best_dist.put(key+3, best_dist.get(key+3) + (double)value * 2/5);
                }
                else if (key - 2 > 0 && key + 3 > 11){
                    best_dist.put(key-2, best_dist.get(key-2) + value);
                }
                else if (key + 3 < 12 && key - 2 < 1){
                    best_dist.put(key+3, best_dist.get(key+3) + value);
                }
                sum += value;
            }
        }
        
        for (Map.Entry<Integer, Double> entry : best_dist.entrySet()) {
            for (int i=0; i< Math.round(entry.getValue() * 15/sum); i++){
                result.add(entry.getKey());
            }
        }
        
        while (result.size() != 15){
            if (result.size() > 15)
                result.remove(Collections.min(result));
            else
                result.add(6);
        }
        while (sum(result) != 90){
            if (sum(result) > 90){
                int max_ind = result.indexOf(Collections.max(result));
                result.set(max_ind, result.get(max_ind)-1);
            }
            if (sum(result) < 90){
                int min_ind = result.indexOf(Collections.min(result));
                result.set(min_ind, result.get(min_ind)+1);
            }
        }
        return result;
    }
    
    
    public void print(String str){
        System.out.println(str);
    }
    
    private float findAverage(List<Integer> line) {
        int sum = 0;
        for (int i : line) sum += i;
        return sum / (float)line.size();
    }
    
    private float findVariance(List<Integer> line) {
        float sum = 0.0F;
        float mean = findAverage(line);
        for (int i : line){
            sum += i * i;
        }
        return sum - mean * mean / line.size();
    }
    
    /**
     * Find out the optimal permutation of our line against the opponent
     * @param row The index of our line used
     * @param opponentRound The list of player skills in the opponent line
     * @return a pair containing score difference and the optimal permutation
     */
    private Pair<Integer, List<Integer>> permutation(int row, List<Integer> opponentRound) {
        ArrayList<ArrayList<Integer>> all_possible = new ArrayList<ArrayList<Integer>>();
        all_possible.add(new ArrayList<Integer>());
        List<Integer> line = distribution.get(row);
        for (int i = 0; i < line.size(); i++) {
            //list of list in current iteration of the array num
            ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();
            
            for (ArrayList<Integer> l : all_possible) {
                // # of locations to insert is largest index + 1
                for (int j = 0; j < l.size()+1; j++) {
                    // + add num[i] to different locations
                    l.add(j, line.get(i));
                    
                    ArrayList<Integer> temp = new ArrayList<Integer>(l);
                    current.add(temp);
                    l.remove(j);
                }
            }
            all_possible = new ArrayList<ArrayList<Integer>>(current);
        }
        int best_score = -6;
        List<Integer> best_lineup = line;
        for (int i=0; i<all_possible.size();i++){
            if (ComputeScore(all_possible.get(i),opponentRound)>best_score){
                best_score = ComputeScore(all_possible.get(i),opponentRound);
                best_lineup = all_possible.get(i);
            }
        }
        return new Pair<Integer, List<Integer>>(best_score,best_lineup);
    }
    
    private int ComputeScore(List<Integer> line1, List<Integer> line2){
        int score = 0;
        for (int i=0; i< line1.size(); i++){
            if (line1.get(i) - line2.get(i) >= 3){
                score += 1;
            }
            if (line2.get(i) - line1.get(i) >= 3){
                score -= 1;
            }
        }
        return score;
    }
    
    private class PlayRow {
        private List<List<Integer>> opponentRemainDist;
        private int maxScore = -16;
        private int bestLine = -1;
        
        private <E> void swap(List<E> list, int i1, int i2) {
            E temp = list.get(i1);
            list.set(i1, list.get(i2));
            list.set(i2, temp);
        }
        
        private void permuteRow(List<Integer> availableRows, int l, int score) {
            if (l != 0)
                score += permutation(availableRows.get(l - 1), opponentRemainDist.get(l - 1)).getKey();
            if (l == availableRows.size()) {
                if (score > maxScore) {
                    maxScore = score;
                    bestLine = availableRows.get(0);
                }
            }
            else {
                for (int i = l; i < availableRows.size(); i++) {
                    swap(availableRows, l, i);
                    permuteRow(availableRows, l + 1, availableRows.size()); 
                    swap(availableRows, l, i); 
                } 
            } 
        }
        
        protected Pair<Integer, List<Integer>> useRows(List<Integer> opponentRound){
            // Predict opponent's line distributions
            Collections.sort(opponentRemainSkills);
            opponentRemainDist = new ArrayList<List<Integer>>();
            opponentRemainDist.add(opponentRound);
            
            for (int i = 0; i < opponentRemainSkills.size(); i += 5) {
                // Prediction policy
                opponentRemainDist.add(new ArrayList<Integer>(
                                                              opponentRemainSkills.subList(i, i + 5)));
            }
            // Finding the best strategy to counter the prediction
            permuteRow(availableRows, 0, 0);
            
            //System.out.println(best_score);
            return new Pair<Integer, List<Integer>>(bestLine, 
                                                    permutation(bestLine, opponentRound).getValue());
        }
    }
    
    
    @Override
    public List<Integer> playRound(List<Integer> opponentRound) {
        List<Integer> round = new ArrayList<Integer>();
        
        if (state){
            for (Integer i : opponentRound) {
                opponentRemainSkills.remove(i);
            }
            Pair<Integer, List<Integer>> temp = new PlayRow().useRows(opponentRound);
            round = temp.getValue();
            availableRows.remove(temp.getKey());
            away_line.add(opponentRound);
        }

        else{
            round = distribution.get(availableRows.get(0));
            availableRows.remove(0);
            if (opponentRound!=null){
                home_line.add(findVariance(opponentRound)-opponentVar);
            }
        }
        
        
        
        return round;
    }
    
    @Override
    public void clear() {
        availableRows.clear();
        for (int i = 0; i < 3; i++)
            availableRows.add(i);
    }
    
}
