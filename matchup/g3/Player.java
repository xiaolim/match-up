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

	private List<Integer> availableRows;

	private Random rand;
	
	private boolean ishome = true; 
	
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

	public List<Integer> getSkills() {
//		for (int i=0; i<7; ++i) {
//			int x = rand.nextInt(11) + 1;
//			skills.add(x);
//			skills.add(12 - x);
//		}
//
//		skills.add(6);
//		Collections.shuffle(skills);
//		
//		return skills;	
    System.out.println("getskills called");
	List<Game> games = History.getHistory();
	ArrayList<Integer> fixed = new ArrayList<Integer>();
	for (int i = 0; i < 5; i++) {
		fixed.add(1);
	}
	
	for (int i = 0; i < 5; i++) {
		fixed.add(8);
	}
	
	for (int i = 0; i < 5; i++) {
		fixed.add(9);
	}
	if(games.size()!=0){
	Game g = games.get(games.size()-1);
	PlayerData pd = (g.playerA.name.equals("g3"))?g.playerB:g.playerA;
	List<Integer> saved = pd.skills;
	Collections.sort(saved);

	if(saved.equals(fixed)){
		for (int i = 0; i < 5; i++) {
			fixed.add(1);
		}
		
		for (int i = 0; i < 5; i++) {
			fixed.add(6);
		}
		
		for (int i = 0; i < 5; i++) {
			fixed.add(11);
		}
	} 
	else{
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
}
	else{
		skills =  fixed;
	}
		
//System.out.println("skills:"+skills);
		return skills;
	}

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
//    	
////     int n = rand.nextInt(availableRows.size());
////     List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
////     availableRows.remove(n);
//     if(opponentRound == null) {
//            int n = rand.nextInt(availableRows.size());
//            List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
//            availableRows.remove(n);
//        	// Collections.shuffle(round);
//        	return round;
//     }
//     int skills = 0, score = 0, ratio = 0, index = 0;
//     List<Integer> result = null;
//     for(int n=0;n<availableRows.size();n++) {
//    	List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
//        List<List<Integer>> res= new ArrayList<>();
//        List<Integer> tempList = new ArrayList<>(); 
//        backtrace(res, tempList, round, 0);   
//        if(n==0) result = res.get(0);
//        for(int i=0;i<result.size();i++) {
//        	if(result.get(i)-opponentRound.get(i)>=3) score++;
//        	if(result.get(i)-opponentRound.get(i)<=-3) score--;
//        	skills = skills+result.get(i);
//        }
//        ratio = score/skills;
//        for(List<Integer> cur: res) {
//            for(int i=0;i<cur.size();i++) {
//            	if(cur.get(i)-opponentRound.get(i)>=3) score++;
//            	if(cur.get(i)-opponentRound.get(i)<=-3) score--;
//            	skills = skills+cur.get(i);
//            }
//            if(score/skills>ratio) {
//            	ratio = score;
//            	result = cur;
//            	index = n;
//            }
//        }
//     }
//     availableRows.remove(index);
//     return result;
    	
    	if (opponentRound == null || !ishome) {
    		ishome = false;
    		int nextRow = availableRows.remove(0);
    		return new ArrayList<Integer>(distribution.get(nextRow));
    	}
    	
//        if(opponentRound == null || !ishome) {
//    	    ishome = false; 
//    	    Collections.sort(availableRows, new Comparator<Integer>(){
//    	    	@Override
//    	    	public int compare(Integer a, Integer b) {
//    	    		List<Integer> roundA = new ArrayList<Integer>(distribution.get(a));
//    	    		List<Integer> roundB = new ArrayList<Integer>(distribution.get(b));
//    	    		int resultA=0, resultB=0;
//    	    		for(int i=0;i<roundA.size();i++) resultA=resultA+roundA.get(i);
//    	    		for(int i=0;i<roundB.size();i++) resultB=resultB+roundB.get(i);
//    	    		return resultA-resultB;
//    	    	}
//    	    });
//    	    List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(0)));
//            availableRows.remove(0);
//        	Collections.shuffle(round);
//        	if(availableRows.size()==0) ishome = true;
//        	return round;
//     }
    	
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

