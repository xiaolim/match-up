package matchup.sim.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerData implements Serializable {
    public String name;

    public List<Integer> skills;
    public List<List<Integer>> distribution;
    public List<List<Integer>> rounds;
    public int score = 0;

    public boolean isHome;

    public PlayerData(String name, boolean isHome) {
        this.name = name;
        this.isHome = isHome;
        skills = new ArrayList<Integer>();
        rounds = new ArrayList<List<Integer>>();
        distribution = new ArrayList<List<Integer>>();
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nSkills: " + skills + "\nDistribution: " + distribution
            + "\nRounds: " + rounds + "\nScore: " + score;
    }
}