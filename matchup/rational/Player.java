package matchup.rational;

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

		for (int i=0; i<3; ++i) availableRows.add(i);
	}
	
    public void init(String opponent) {
    }

	public List<Integer> getSkills() {
/*		for (int i=0; i<7; ++i) {
			int x = rand.nextInt(11) + 1;
			skills.add(x);
			skills.add(12 - x);
		}

		skills.add(6);
*/
		skills.clear();
                for(int i=0;i<4;i++)
                skills.add(9);

		 for(int i=0;i<5;i++)
                skills.add(7);

		 for(int i=0;i<5;i++)
                skills.add(3);

                skills.add(4);
		
		Collections.shuffle(skills);

		return skills;
	}

    public List<List<Integer>> getDistribution(List<Integer> opponentSkills, boolean isHome) {
    	List<Integer> index = new ArrayList<Integer>();
 //   	for (int i=0; i<15; ++i) index.add(i);

    	distribution = new ArrayList<List<Integer>>();

//		Collections.shuffle(index);

		List<Integer> row1 = new ArrayList<Integer>();
		row1.add(9);
		row1.add(9);
		row1.add(7);
		row1.add(3);
		row1.add(3);

		List<Integer> row2 = new ArrayList<Integer>();
		row2.add(9);
		row2.add(7);
		row2.add(7);
		row2.add(4);
		row2.add(3);

		List<Integer> row3 = new ArrayList<Integer>();
		row3.add(9);
		row3.add(7);
		row3.add(7);
		row3.add(3);
		row3.add(3);

/*		int n = 0;
    	for (int i=0; i<3; ++i) {
    		List<Integer> row = new ArrayList<Integer>();
    		for (int j=0; j<5; ++j) {
    			row.add(skills.get(index.get(n)));
    			++n;
    		} */

    		distribution.add(row1);
		distribution.add(row2);
		distribution.add(row3);
    	

    	return distribution;
    }


    public int canWin(int score,List<Integer> round,int j)
    {

	for(int i=j;i<round.size();i++)
		if(round.get(i) - score > 2)
		 	return i;
		return -1;
    }

    public int canTie(int score,List<Integer> round,int j)
    {
	for(int i=j;i<round.size();i++)
		if(Math.abs(score - round.get(i)) < 3)
		 	return i;
		return -1;
    }

    public List<Integer> playRound(List<Integer> opponentRound) {
 
	if(opponentRound == null)
	{
		int n = rand.nextInt(availableRows.size());

    		List<Integer> Awayround = new ArrayList<Integer>(distribution.get(availableRows.get(n)));
    		availableRows.remove(n);

    		Collections.shuffle(Awayround);

    		return Awayround;
	}

//	System.out.println("rational playing home strategy");
	int n = availableRows.size();
        int[] score = new int[n];
        ArrayList<List<Integer>> Candidates = new ArrayList<List<Integer>>();
	for(int i=0;i<n;i++)
	{
		List<Integer> round = new ArrayList<Integer>(distribution.get(availableRows.get(i)));
		
		Collections.sort(round);
		
		for(int j=0;j<opponentRound.size();j++)
		{
			
/// try to win
			int idx_win = canWin(opponentRound.get(j),round,j);
			if(idx_win>0)
			{
				
				Collections.swap(round,j,idx_win);
				if(j<opponentRound.size()-1)
				Collections.sort(round.subList(j+1,round.size()));
				score[i]++;
				continue;
			}
							
/// try to tie
			int idx_tie = canTie(opponentRound.get(j),round,j);
			if(idx_tie>0)
			{
				
				Collections.swap(round,j,idx_tie);
				if(j<opponentRound.size()-1)
				Collections.sort(round.subList(j+1,round.size()));
				continue;
			}

/// try to lose with least
			score[i]--;
			
		}
		
		Candidates.add(round);

	}

//		System.out.println("Candidates populated");
//		System.out.println(Candidates);	

/// decide which row to play

	int min_win = 100000;
	int min_loss = -100000;
	int win_choice =-1;
	int tie_choice = -1;
	int loss_choice = -1;

	for(int i=0;i<n;i++)
	{
		if(score[i]<min_win && score[i]>0)
		{
			min_win = score[i];
			win_choice = i;
		}

		else if(score[i] == 0) tie_choice = i;

		else if(score[i] > min_loss && score[i]<0)
		{
			min_loss = score[i];
			loss_choice = i;
		}
	}

//	for(int i=0;i<n;i++) System.out.println("scores are " + Integer.toString(score[i]));
	
	int fin_choice = -1;

	if(win_choice >= 0)
		{
//			System.out.println("winning this matchup");
			fin_choice = win_choice;
			availableRows.remove(win_choice);
		}
	else if(tie_choice >= 0)
		{
//			System.out.println("tying this matchup");
			fin_choice = tie_choice;
			availableRows.remove(tie_choice);
		}
	else
		{
//			System.out.println("losing this matchup");
			fin_choice = loss_choice;
			System.out.println(fin_choice);
			availableRows.remove(loss_choice);
		}

//	System.out.println("Determine final choice");
        List<Integer> PlayedRow = new ArrayList<Integer>(Candidates.get(fin_choice));
	
    	return PlayedRow;
    }

    public void clear() {
    	availableRows.clear();
    	for (int i=0; i<3; ++i) availableRows.add(i);
    }
}
