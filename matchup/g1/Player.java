package matchup.g1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import matchup.sim.utils.*;

public class Player implements matchup.sim.Player {
    private List<Integer> skills;
    private List<Integer> bestTeam;
    private List<Integer> opponentTeam;
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
	    skills = new ArrayList<Integer>();
	    //Uniform
	    //Collections.addAll(skills, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 2, 2, 10, 10);

            //Risky
	    //Collections.addAll(skills, 11, 11, 1, 1, 6);
	    //Collections.addAll(skills, 11, 11, 1, 1, 6);
	    //Collections.addAll(skills, 11, 11, 1, 1, 6);

	    //Safe
	    //Collections.addAll(skills, 8, 8, 4, 4, 6);
	    //Collections.addAll(skills, 8, 8, 4, 4, 6);
	    //Collections.addAll(skills, 8, 8, 4, 4, 6);
	    
	    //Middle
	    Collections.addAll(skills, 10, 2, 9, 3, 6);
	    Collections.addAll(skills, 10, 2, 9, 3, 6);
	    Collections.addAll(skills, 10, 2, 9, 3, 6);
	    
	    //Old
	    //Collections.addAll(skills, 11, 11, 1, 1, 6, 8, 8, 4, 4, 6, 10, 2, 9, 3, 6);
	    return skills;
    }

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	List<Integer> index = new ArrayList<Integer>();
    	for (int i=0; i<15; ++i) index.add(i);

    	distribution = new ArrayList<List<Integer>>();

        //Collections.shuffle(index, rand);
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
	if (opponentRound == null) {
	    opponentRound = new ArrayList<Integer>();
	    Collections.addAll(opponentRound, 1, 2, 6, 10, 11);
	}
	//System.out.println("inside playRound");
    	//System.out.println("oppTeam:");
	//System.out.println(opponentRound);
	//System.out.println("bestTeam:");
	//System.out.println(bestTeam);
	int n = rand.nextInt(availableRows.size());

    	List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
    	availableRows.remove(n);

	bestTeam = new ArrayList<Integer>();
	for (int i : round) {
	    Collections.addAll(bestTeam, i);
	}
	//Collections.addAll(bestTeam, 0, 0, 0, 0, 0);
	//System.out.println("bestTeam before permute:");
	//System.out.println(bestTeam);

	round = permuteHomeTeam(round, opponentRound);
	//System.out.println("oppTeam:");
	//System.out.println(opponentRound);
	System.out.println("bestTeam:");
	System.out.println(bestTeam);
	System.out.println("round:");
	System.out.println(round);
	return bestTeam;
	//round = bestTeam;

    	//return round;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);

        // Get history of games.
        List<Game> games = History.getHistory();
	int friendly_away_score = 0;
	int opponent_away_score = 0;
	int friendly_away_wins = 0;
	int opponent_away_wins = 0;
	int friendly_home_score = 0;
	int opponent_home_score = 0;
	int friendly_home_wins = 0;
	int opponent_home_wins = 0;
	int friendly_total_score = 0;
	int opponent_total_score = 0;
	int friendly_total_wins = 0;
	int opponent_total_wins = 0;
	int numGames = 20000;
	//int count_playerA_friendly = 0;
	if (games.size() >= numGames) {
	    System.out.println(games.size());
	    Game game;
	    PlayerData opponent;
	    PlayerData friendly;
	    for (int i = 0; i < numGames; i++) {
		game = games.get(i);
		if (game.playerA.name == "g1") {
		    friendly = game.playerA;
		    opponent = game.playerB;
		    //count_playerA_friendly += 1;
		} else {
		    friendly = game.playerB;
		    opponent = game.playerA;
		}
		if (friendly.score > opponent.score) {
		    friendly_total_wins += 1;
		} else if (friendly.score < opponent.score) {
		    opponent_total_wins += 1;
		}
		friendly_total_score += friendly.score;
		opponent_total_score += opponent.score;
		if (friendly.isHome) {
		    if (friendly.score > opponent.score) {
			friendly_home_wins += 1;
		    } else if (friendly.score < opponent.score) {
			opponent_home_wins += 1;
		    }
		    friendly_home_score += friendly.score;
		    opponent_home_score += opponent.score;
		}
		if (!friendly.isHome) {
		    if (friendly.score > opponent.score) {
			friendly_away_wins += 1;
		    } else if (friendly.score < opponent.score) {
			opponent_away_wins += 1;
		    }
		    friendly_away_score += friendly.score;
		    opponent_away_score += opponent.score;
		}
	    }
	    System.out.println("end result:");

	    System.out.println("home:");
	    System.out.println("friendly result:");
	    System.out.println("score:");
	    System.out.println(friendly_home_score);
	    System.out.println("wins:");
	    System.out.println(friendly_home_wins);
	    System.out.println("opponent result:");
	    System.out.println("score:");
	    System.out.println(opponent_home_score);
	    System.out.println("wins:");
	    System.out.println(opponent_home_wins);
	    System.out.println("away:");
	    System.out.println("friendly result:");
	    System.out.println("score:");
	    System.out.println(friendly_away_score);
	    System.out.println("wins:");
	    System.out.println(friendly_away_wins);
	    System.out.println("opponent result:");
	    System.out.println("score:");
	    System.out.println(opponent_away_score);
	    System.out.println("wins:");
	    System.out.println(opponent_away_wins);
	    System.out.println("friendly result:");
	    //System.out.println("count_playerA_friendly:");
	    //System.out.println(count_playerA_friendly);
	    System.out.println("score:");
	    System.out.println(friendly_total_score);
	    System.out.println("wins:");
	    System.out.println(friendly_total_wins);
	    System.out.println("opponent result:");
	    System.out.println("score:");
	    System.out.println(opponent_total_score);
	    System.out.println("wins:");
	    System.out.println(opponent_total_wins);
	}
    }

    public List<Integer> permuteHomeTeam(List<Integer> homeTeam, List<Integer> awayTeam){
       	//System.out.println("homeTeam:");
	//System.out.println(homeTeam);
       	//System.out.println("awayTeam:");
	//System.out.println(awayTeam);
	//System.out.println("bestTeam:");
	//System.out.println(bestTeam);
        if(checkLineupScore(homeTeam, awayTeam) == 5){
            return bestTeam;
        }
       	//System.out.println("homeTeam:");
	//System.out.println(homeTeam);
       	//System.out.println("awayTeam:");
	//System.out.println(awayTeam);
	//System.out.println("bestTeam:");
	//System.out.println(bestTeam);
	permute(homeTeam, awayTeam);

        //System.out.println(checkLineupScore(homeTeam, awayTeam));
        return bestTeam;
    }

    public void permute(List<Integer> arr, List<Integer> away){
        permuteHelper(arr, 0, away);
    }

    public void permuteHelper(List<Integer> arr, int index, List<Integer> away){
	//get rid of temp?
        if(index >= arr.size() - 1){
            int temp = 5;
        }

        for(int i = index; i < arr.size(); i++){
            if(checkLineupScore(arr, away) > checkLineupScore(bestTeam, away)){
                for(int j=0; j < arr.size();j++){
                    bestTeam.set(j, arr.get(j));
                }
            }
            Collections.swap(arr, index, i);
            permuteHelper(arr, index+1, away);
            Collections.swap(arr, index, i);
        }
    }

    public static int checkLineupScore(List<Integer> homeTeam, List<Integer> awayTeam){
	// optimize later
        int score = 0;
        for(int i = 0; i < homeTeam.size(); i++){
            if(homeTeam.get(i) > awayTeam.get(i) + 2){
                score++;
            } else if(awayTeam.get(i) > homeTeam.get(i) + 2) {
                score--;
            }
        }
        return score;
    }


}
