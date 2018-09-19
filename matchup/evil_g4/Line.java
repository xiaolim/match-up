package matchup.evil_g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

public class Line extends java.util.ArrayList<Integer> {

	private int highScore;
	private Line record;
    
    public Line() {
    	super(5);
    }

    public Line(Collection<? extends Integer> c) {
        super(c);
    }

    public int scoreAgainst(Line opponent) {
    	int tally = 0;

        for (int i = 0; i < size(); ++i) {
        	int diff = this.get(i) - opponent.get(i);
            if (diff >= 3) { ++tally; }
            else if (diff <= -3) { --tally; } 
        }

        return tally;
    }

    public double scoreWeighted(Line opponent) {
    	int sum = 0;
        for (int elt: this) {sum += elt;}
        return scoreAgainst(opponent) / (double)sum;
    }

    public void permuteFor(Line opponent) {
    	record = new Line(this);
    	highScore = Integer.MIN_VALUE;
        permuteHelper(opponent, 0);
        clear();
        for (int elt: record) {add(elt);}
    }

    private void permuteHelper(Line opponent, int idx) {
    	//System.out.print(idx);System.out.println(size());
    	if (idx >= size()-1) {
    		//System.out.print(idx);
            int score = scoreAgainst(opponent);
            if (score > highScore) { 
            	record.clear();
            	//System.out.println(size());
            	for (int elt: this) {record.add(elt);}
                highScore = score;
            }
    	} else {
    		for (int i = idx; i < size(); ++i) {
    			int tmp = get(i);
    			set(i, get(idx));
    			set(idx, tmp);

    			permuteHelper(opponent, idx+1);

    			tmp = get(idx);
    			set(idx, get(i));
    			set(i, tmp);
    		}
    	}
    }

}