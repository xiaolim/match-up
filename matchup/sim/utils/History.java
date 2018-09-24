package matchup.sim.utils;

import java.util.List;
import matchup.sim.Simulator;

public class History {
    public static List<Game> getHistory() {
        return Simulator.getGames();
    }

    public static Game getLastGame() {
        return Simulator.getLastGame();
    }
}
