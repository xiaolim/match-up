package matchup.g8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Player implements matchup.sim.Player 
{
	private List<Integer> skills;
	private List<List<Integer>> distribution;
	private List<Integer> availableRows;
	private Random rand;
	// Keep track of what the highest ranked player was and build around that...
	private Integer highest_ranked_player;
	
	public Player() 
	{
		rand = new Random();
		skills = new ArrayList<Integer>();
		distribution = new ArrayList<List<Integer>>();
		availableRows = new ArrayList<Integer>();
		for (int i = 0;i < 3; ++i)
		{
			availableRows.add(i);
		}
	}
	
    public void init(String opponent) 
    {
    	
    }

	public List<Integer> getSkills()
	{
		// I need to check what the highest ranked player is...
		// So I need to know whether I must draw/beat their highest ranked player for victory
		
		List<Integer> skills = new ArrayList<>();
		skills.add(6); // idx: 0
		for (int i = 0; i < 7 i++) 
		{
			skills.add(7);// idx: 1, 3, 5, 7, 9, 11, 13
			skills.add(5);// idx: 2, 4, 6, 8, 10, 12, 14
		}
		return skills;
	}

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) 
    {
    	// Ace
    	List<Integer> row1 = new ArrayList<Integer>();
    	// Medium
    	List<Integer> row2 = new ArrayList<Integer>();
    	// Lowest
    	List<Integer> row3 = new ArrayList<Integer>();
    
    	// Do I get to build a new list when I am home?
    	// Of course I assume it is same distribution!
    	if(isHome)
    	{
        	row1.add(7);
        	row1.add(7);
        	row1.add(7);
        	row1.add(5);
        	row1.add(5);

        	row2.add(7);
        	row2.add(7);
        	row2.add(6);
        	row2.add(5);
        	row2.add(5);

        	row3.add(7);
        	row3.add(7);
        	row3.add(5);
        	row3.add(5);
        	row3.add(5);
    	}
    	else
    	{
        	row1.add(7);
        	row1.add(7);
        	row1.add(7);
        	row1.add(7);
        	row1.add(7);

        	row2.add(7);
        	row2.add(7);
        	row2.add(6);
        	row2.add(5);
        	row2.add(5);

        	row3.add(5);
        	row3.add(5);
        	row3.add(5);
        	row3.add(5);
        	row3.add(5);
    	}
    
    	distribution = new ArrayList<List<Integer>>();
    	distribution.add(row1);
    	distribution.add(row2);
    	distribution.add(row3);
    	return distribution;
    }


    public int canWin(int score, List<Integer> round, int j)
    {
    	for(int i = j; i < round.size(); i++)
		{
    		if(round.get(i) - score > 2)
    		{
    		 	return i;
    		}
		}
		return -1;
    }

    public int canTie(int score, List<Integer> round, int j)
    {
    	for(int i=j;i<round.size();i++)
    	{
    		if(Math.abs(score - round.get(i)) < 3)
		 	{
    			return i;
		 	}
    	}
    	return -1;
    }

    public List<Integer> playRound(List<Integer> opponentRound) 
    {
    	List<Integer> round = null;
    	if (opponentRound == null)
    	{
    		int min_sum = 60;
    		int min_row = -1;

    		for (int i = 0; i < availableRows.size(); i++)
    		{
    			int sum = 0;
    			for(int j: distribution.get(i)) 
    			{
    				sum += j;
    			}

    			if (sum < min_sum)
    			{
    				min_sum = sum;
    				min_row = i;
    			}
    		}
    		round = distribution.get(min_row);
    		availableRows.remove(min_row);
    		return round;
    	}

    	//	System.out.println("rational playing home strategy");
    	int n = availableRows.size();
    	int [] score = new int[n];
    	ArrayList<List<Integer>> Candidates = new ArrayList<List<Integer>>();
    	for(int i = 0; i < n;i++)
    	{
    		round = new ArrayList<Integer>(distribution.get(availableRows.get(i)));
    		Collections.sort(round);

    		for(int j = 0; j < opponentRound.size();j++)
    		{
    			/// try to win
    			int idx_win = canWin(opponentRound.get(j),round,j);
    			if(idx_win>0)
    			{
    				Collections.swap(round, j, idx_win);
    				if(j<opponentRound.size()-1)
    				{
    					Collections.sort(round.subList(j+1,round.size()));
    				}
    				score[i]++;
    				continue;
    			}

    			/// try to tie
    			int idx_tie = canTie(opponentRound.get(j),round,j);
    			if(idx_tie>0)
    			{

    				Collections.swap(round,j,idx_tie);
    				if(j<opponentRound.size()-1)
    				{
    					Collections.sort(round.subList(j+1,round.size()));
    				}
    				continue;
    			}
    			// try to lose with least
    			score[i]--;
    		}
    		Candidates.add(round);
    	}
    	// decide which row to play
    	int min_win = 100000;
    	int min_loss = -100000;
    	int win_choice =-1;
    	int tie_choice = -1;
    	int loss_choice = -1;

    	for(int i = 0; i < n;i++)
    	{
    		if(score[i]<min_win && score[i]>0)
    		{
    			min_win = score[i];
    			win_choice = i;
    		}

    		else if(score[i] == 0)
    		{
    			tie_choice = i;
    		}

    		else if(score[i] > min_loss && score[i]<0)
    		{
    			min_loss = score[i];
    			loss_choice = i;
    		}
    	}

    	int fin_choice = -1;

    	if(win_choice >= 0)
    	{
    		// System.out.println("winning this matchup");
    		fin_choice = win_choice;
    		availableRows.remove(win_choice);
    	}
    	else if(tie_choice >= 0)
    	{
    		// System.out.println("tying this matchup");
    		fin_choice = tie_choice;
    		availableRows.remove(tie_choice);
    	}
    	else
    	{
    		// System.out.println("losing this matchup");
    		fin_choice = loss_choice;
    		System.out.println(fin_choice);
    		availableRows.remove(loss_choice);
    	}

    	//	System.out.println("Determine final choice");
    	List<Integer> PlayedRow = new ArrayList<Integer>(Candidates.get(fin_choice));
    	return PlayedRow;
    }

    public void clear() 
    {
    	availableRows.clear();
    	for (int i = 0; i < 3; ++i)
    	{
    		availableRows.add(i);
    	}
    }
}
