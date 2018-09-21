package matchup.evil_g4;

import java.util.Collections;
import java.util.Collection;

public class Skills extends java.util.ArrayList<Integer> {

    public Skills() {
        super(15);
    }

    public Skills(Collection<? extends Integer> c) {
        super(c);
    }

    private void groupHelper(int balance) {
        Collections.sort(this);

        if (balance > 2) {
            Collections.swap(this, 1, 9);
            Collections.swap(this, 5, 13);
        }

        if (balance > 1) {
            Collections.swap(this, 2, 12);
        }

        if (balance > 0) {
            Collections.swap(this, 4, 6);
            Collections.swap(this, 8, 10);
        }
    }

    public void groupForHome() {
    	groupHelper(3);
    }

    public void groupForAway() {
        groupHelper(0);
    }
}