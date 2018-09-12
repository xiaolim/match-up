package matchup.sim;

import java.util.List;

public interface Player {
    // Initialization function.
    // opponent: Name of the opponent.
    public void init(String opponent);

    // Gets skills of all 15 players.
    public List<Integer> getSkills();

    // Gets distribution of skills for each round.
    // Returns an array of size 3x15.
    // isHome: indicates whether player is home or away.
    public List<List<Integer>> getDistribution(
        List<Integer> opponentSkills, boolean isHome);

    // Play your round.
    // Returns list of player skills 
    public List<Integer> playRound(List<Integer> opponentRound);

    // Cleans up the player's state before starting the next game of the pair.
    public void clear();
}
