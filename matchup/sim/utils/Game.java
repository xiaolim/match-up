package matchup.sim.utils;

import java.io.Serializable;

public class Game implements Serializable {
    public PlayerData playerA;
    public PlayerData playerB;

    public Game(String playerAName, String playerBName, boolean isHome) {
        this.playerA = new PlayerData(playerAName, isHome);
        this.playerB = new PlayerData(playerBName, !isHome);
    }

    @Override
    public String toString() {
        return playerA.toString() + "\n" + playerB.toString() + "\n";
    }
}