package matchup.g3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Comparator;
import matchup.sim.utils.*;
import matchup.sim.Simulator;

public class Player implements matchup.sim.Player {
	private List<Integer> skills;
	private List<List<Integer>> distribution;
    private static int game_num=0;
	private List<Integer> availableRows;
	private Random rand;
	private Integer period = -1;
	private boolean ishome = true;

	//g8 par
    private List<Integer> prevskills;
    private List<Integer> Opponent;
    private boolean playHome,  first_game;
    //g8 par
    public List<Integer> getNewPeriod()
    {
        List<Game> Games = History.getHistory();
        Game g = Games.get(Games.size()-1);
        List<Integer> sk = g.playerA.name.equals("g3")?g.playerA.skills:g.playerB.skills;
        List<Integer> oppSkills = sk;
        Collections.sort(oppSkills);
        Integer Count[] = new Integer[11];
		for(int i=0;i<11;i++) Count[i] =0;
        for(int i=0;i<period;i++){
            g = Games.get(Games.size()-i-1);
            sk = g.playerA.name.equals("g3")?g.playerA.skills:g.playerB.skills;
            for(int j=0;j<sk.size();j++){
		//		System.out.println(j+"  "+sk.size());
                Count[sk.get(j)-1]++;

            }
        }

        if(Count[9] + Count[10] <= 4*period && Count[4] + Count[6] + Count[7] <= 10*period) /// Mostly 9's are highest
            oppSkills = playSevens();
        else if(Count[6]>=8*period) 					///opponent using the sevens strategy
            oppSkills = playAntiSevens();
        else if(Count[8] + Count[7] <= 6*period)			/// If somewhat balanced
            oppSkills = playBalanced();
        else 							/// improve upon opponent's distribution
            for(int i=0;i<15;i++)
            {
                if(i<6)
                    oppSkills.set(i,oppSkills.get(i)+3);
                else
                    oppSkills.set(i,oppSkills.get(i)-2);
            }
		//System.out.println("3");
        Opponent = oppSkills;
        return oppSkills;
    }

    public Player() {
		rand = new Random();
		skills = new ArrayList<Integer>();
		distribution = new ArrayList<List<Integer>>();
		availableRows = new ArrayList<Integer>();
		ishome = false;

		for (int i=0; i<3; ++i) availableRows.add(i);
	}



    public void init(String opponent) {
    }

    private double calVar(List<Integer> s){
	    double sum = 0;
	    double sqSum = 0;
	    for(Integer i : s)
	        sum = sum + i;
        double average = sum/15.0;
        for(Integer i : s){
            System.out.print(i);
            sqSum = sqSum + (i-average)*(i-average);
        }
        return sqSum/15;
    }
	public List<Integer> getSkills() {
	skills.clear();
	List<Game> games = History.getHistory();
	game_num = games.size();
	ArrayList<Integer> fixed = new ArrayList<>();
	if(game_num == 0){
	    for(int i=0; i<3 ;i++){
	            fixed.add(10);
	            fixed.add(4);
	            fixed.add(7);
	            fixed.add(3);
	            fixed.add(6);
        }
        skills = fixed;
	    return skills;
    }
	if(game_num==1){
        for(int i=0; i<5 ;i++){
            fixed.add(2);
        }
		for(int i=0; i<5 ;i++){
			fixed.add(6);
		}
		for(int i=0; i<5 ;i++){
			fixed.add(10);
		}
        skills = fixed;
        return skills;
    }
    if(game_num==2){
			for(int i=0; i<5 ;i++){
				fixed.add(1);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(8);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(9);
			}
			skills = fixed;
			return skills;
    }
    if(game_num==3){
			for(int i=0; i<5 ;i++){
				fixed.add(2);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(7);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(9);
			}
			skills = fixed;
			return skills;
    }
    if(game_num==4){
			for(int i=0; i<5 ;i++){
				fixed.add(2);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(6);
			}
			for(int i=0; i<5 ;i++){
				fixed.add(10);
			}
			skills = fixed;
			return skills;
    }
   // System.out.println(games.size()-1);
    Game g_end = History.getLastGame();
   // System.out.println(games.size()-3);
	Game g_start = games.get(games.size()-3); // something went wrong with the simulator
	PlayerData pd_e = (!g_end.playerA.name.equals("g3"))?g_end.playerA:g_end.playerB;
	PlayerData pd_s = (!g_start.playerA.name.equals("g3"))?g_start.playerA:g_start.playerB;
	List<Integer> sk_e=pd_e.skills;
	List<Integer> sk_s=pd_s.skills;
    System.out.println(sk_e);
    System.out.println(sk_s);
    System.out.println(games.size());
	if(game_num>4 && game_num<10 && Math.abs(calVar(sk_e)-calVar(sk_s))>=(2.0/3) ){
	    for(int i=0 ;i< 15; i++){

	        fixed.add(6);

        }

	    skills = fixed;
	    return skills;
    }
    if(period ==-1 && game_num<=6)
    	period = 1;
    if(period ==-1)
        period = game_num-7;

    System.out.println("here");
    sk_e = getNewPeriod();
    skills = getNew();
    return skills;
	}
    //g8 counter start
    private List<Integer> playSevens()
    {
        List<Integer> skillset = new ArrayList<Integer>();
        for(int i=0;i<10;i++) skillset.add(7);
        for(int i=0;i<3;i++) skillset.add(5);
        skillset.add(3);
        skillset.add(2);
        return skillset;
    }

    private List<Integer> playAntiSevens()
    {
        List<Integer> skillset = new ArrayList<Integer>();
        for(int i=0;i<8;i++) skillset.add(5);
        for(int i=0;i<3;i++) skillset.add(10);
        skillset.add(7);
        skillset.add(7);
        skillset.add(5);
        skillset.add(1);
        return skillset;
    }

    private List<Integer> playBalanced()
    {
        List<Integer> skillset = new ArrayList<Integer>();
        for(int i=0;i<4;i++) skillset.add(9);
        for(int i=0;i<3;i++) skillset.add(7);
        for(int i=0;i<3;i++) skillset.add(5);
        for(int i=0;i<4;i++) skillset.add(4);
        skillset.add(2);
        return skillset;
    }

    public List<Integer> getNew()
    {
        List<Integer> oppSkills = Opponent;
        Collections.sort(oppSkills);
        Integer Count[] = new Integer[11];
        for(int i=0;i<11;i++) Count[i] =0;

        for(int i=0;i<oppSkills.size();i++)
        {
            Count[oppSkills.get(i)-1]++;
        }
        if(Count[9] + Count[10] <= 4 && Count[4] + Count[6] + Count[7] <= 10) /// Mostly 9's are highest
            oppSkills = playSevens();
        else if(Count[6]>=8) 					///opponent using the sevens strategy
            oppSkills = playAntiSevens();
        else if(Count[8] + Count[7] <= 6)			/// If somewhat balanced
            oppSkills = playBalanced();
        else 							/// improve upon opponent's distribution
            for(int i=0;i<15;i++)
            {
                if(i<6)
                    oppSkills.set(i,oppSkills.get(i)+3);
                else
                    oppSkills.set(i,oppSkills.get(i)-2);
            }


        return oppSkills;
    }

    private void improveSkills()
    {
        Collections.sort(skills);

        for(int i=0;i<15;i++)
        {
            if(i<6)
                skills.set(i,skills.get(i)+3);
            else
                skills.set(i,skills.get(i)-2);
        }

    }
//    private void Analyze()
//    {
//
//        List<Game> hist  = Simulator.getGames();
//        if(hist.size()>=2 && hist.get(hist.size()-1).playerA.name == "g3")
//        {
//            Opponent = hist.get(hist.size()-1).playerB;
//            Opponent.score = hist.get(hist.size()-1).playerB.score + hist.get(hist.size()-2).playerB.score;
//            Self = hist.get(hist.size()-1).playerA;
//            Self.score = hist.get(hist.size()-1).playerA.score + hist.get(hist.size()-2).playerA.score;
//        }
//        else if(hist.size()>=2)
//        {
//            Opponent = hist.get(hist.size()-1).playerA;
//            Opponent.score = hist.get(hist.size()-1).playerA.score + hist.get(hist.size()-2).playerA.score;
//            Self = hist.get(hist.size()-1).playerB;
//            Self.score = hist.get(hist.size()-1).playerB.score + hist.get(hist.size()-2).playerB.score;
//        }
//
//    }
    //g8 counter end
    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	quickSort(0, skills.size() - 1);
    	List<Integer> index = new ArrayList<Integer>();
    	for (int i=0; i<15; ++i) index.add(i);

    	distribution = new ArrayList<List<Integer>>();

		if (isHome) {
			ishome = true;
			distribution =  varyTeamSkills();
			return distribution;
		}
		
		ishome = false;
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

    	
    	if (opponentRound == null || !ishome) {
    		ishome = false;
    		int nextRow = availableRows.remove(0);
    		return new ArrayList<Integer>(distribution.get(nextRow));
    	}

    	ishome = true;
    	int[][] teams = new int[availableRows.size()][5];
    	int[] wins = new int[availableRows.size()];
    	double[] wastes = new double[availableRows.size()];
    	double[] efficiencies = new double[availableRows.size()];
    	for (int i = 0; i < availableRows.size(); i++) {
    		List<Integer> team = distribution.get(availableRows.get(i));
    		boolean[] marked = new boolean[5];
    		double totalSkillsLost = 0;
    		int totalNetWin = 0;
    		for (int j = 0; j < team.size(); j++) {
    			Map<Stat, ExtrinsicPQ<Integer>> map = new EnumMap<Stat, ExtrinsicPQ<Integer>>(Stat.class);
    			for (int e = 0; e < opponentRound.size(); e++) {
    				if (marked[e]) {
    					continue;
    				}
    				
    				int diff = team.get(j) - opponentRound.get(e);
    				if (diff >= 3) {
    					// win, (diff - 3) is the amount of waste of skills
    					if (!map.containsKey(Stat.WIN)) {
    						ExtrinsicPQ<Integer> order = new ArrayHeap<Integer>();
    						order.insert(e, diff - 3);
    						map.put(Stat.WIN, order);
    					}
    					else {
    						map.get(Stat.WIN).insert(e, diff - 3);
    					}
    				}
    				else if (diff >= -2 && diff <= 2) {
    					// tie, (diff + 2) is the amount of waste of skills
    					if (!map.containsKey(Stat.TIE)) {
    						ExtrinsicPQ<Integer> order = new ArrayHeap<Integer>();
    						order.insert(e, diff + 2);
    						map.put(Stat.TIE, order);
    					}
    					else {
    						map.get(Stat.TIE).insert(e, diff + 2);
    					}
    				}
    				else {
    					// lose, (diff + 3) is the amount of waste of skills, in the case of losing, it's usually a gain of skills
    					if (!map.containsKey(Stat.LOSE)) {
    						ExtrinsicPQ<Integer> order = new ArrayHeap<Integer>();
    						order.insert(e, diff + 3);
    						map.put(Stat.LOSE, order);
    					}
    					else {
    						map.get(Stat.LOSE).insert(e, diff + 3);
    					}
    				}
    			}
    			
    			Stat status;
    			if (map.containsKey(Stat.WIN)) {
    				totalNetWin += 1;
    				status = Stat.WIN;
    			}
    			else if (map.containsKey(Stat.TIE)) {
    				status = Stat.TIE;
    			}
    			else {
    				totalNetWin -= 1;
    				status = Stat.LOSE;
    			}
    			
    			totalSkillsLost += map.get(status).peekPriority();
    			int index = map.get(status).peek();
    			marked[index] = true;
    			teams[i][index] = team.get(j);
    		}
    		wins[i] = totalNetWin;
    		wastes[i] = totalSkillsLost;
    		if (wins[i] < 0 && wastes[i] > 0) {
    			wins[i] = - wins[i];
    			wastes[i] *= 4;
    		}
    		
    		if (wins[i] > 0 && wastes[i] < 0) {
    			wastes[i] = - wastes[i] / 4;
    		}
    		efficiencies[i] = (wins[i] + 0.001) / (wastes[i] + 0.0001);
    	}
    	
    	// find the positive wins and negative wastes
    	double score = - Double.MAX_VALUE;
    	int index = -1;
    	List<Integer> result = new ArrayList<Integer>();
    	for (int i = 0; i < efficiencies.length; i++) {
    		if (efficiencies[i] > score) {
    			index = i;
    			score = efficiencies[i];
    		}
    	}

    	System.out.println("available rows: " + availableRows + ", index: " + index);
    	for (int i = 0; i < teams[index].length; i++) {
    		result.add(teams[index][i]);
    	}
    	availableRows.remove(index);
    	System.out.println("after removal: " + availableRows);
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
    
    private void quickSort(int low, int high) {
    	int i = low;
    	int j = high;
    	int pivot = skills.get(low + (high - low) / 2);
    	while (i <= j) {
    		while (skills.get(i) < pivot) {
    			i++;
    		}
    		while (skills.get(j) > pivot) {
    			j--;
    		}
    		
    		if (i <= j) {
    			int temp = skills.get(i);
    			skills.set(i, skills.get(j));
    			skills.set(j, temp);
    			i++;
    			j--;
    		}
    	}
    	
    	if (low < j) {
    		quickSort(low, j);
    	}
    	if (i < high) {
    		quickSort(i, high);
    	}
    }
    
    private List<List<Integer>> varyTeamSkills() {
    	List<List<Integer>> varied = new ArrayList<List<Integer>>();
    	
    	for (int i = 0; i < 3; i++) {
    		List<Integer> row = new ArrayList<Integer>();
    		for (int j = 0; j < 5; j++) {
    			row.add(skills.get(i + j * 3));
    		}
    		varied.add(row);
    	}
    	
    	return varied;
    }
    
    private enum Stat {
    	WIN, TIE, LOSE
    }
}

