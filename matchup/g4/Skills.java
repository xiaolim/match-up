package matchup.g4;

import java.util.Collections;
import java.util.Collection;

public class Skills extends java.util.ArrayList<Integer> {

	private int highScore;
    private Skills record;

    public Skills() {
        super(15);
    }

    public Skills(Collection<? extends Integer> c) {
        super(c);
    }

    public void groupForHome(int groupSize) {
    	int numGroups = size()/groupSize;
    	int size = size();
        record = new Skills(this);
        highScore = Integer.MIN_VALUE;
        clear();
        Collections.sort(record);

        for (int i = 0; i < numGroups; ++i) {
        	for (int j = 0; j < size; j += numGroups) {
        		add(record.get(i+j));
        	}
        }
    }

    public void groupForAway(int groupSize) {
        record = new Skills(this);
        highScore = Integer.MIN_VALUE;
        clear();
        Collections.sort(record);
        for (int elt: record) {add(elt);}
    }
}