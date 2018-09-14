package matchup.sim.utils;

public class Game {
    public PlayerData playerA;
    public PlayerData playerB;

    public Game(String playerAName, String playerBName, boolean isHome) {
        this.playerA = new PlayerData(playerAName, isHome);
        this.playerB = new PlayerData(playerBName, !isHome);
    }
}