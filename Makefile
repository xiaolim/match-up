all:
	java matchup.sim.Simulator -p random random --gui --fps 1

compile:
	javac matchup/sim/utils/PlayerData.java
	javac matchup/sim/utils/Game.java
	javac matchup/sim/utils/History.java
	javac matchup/sim/*.java

clean:
	rm matchup/*/*.class
	rm matchup/sim/*/*.class

run:
	java matchup.sim.Simulator -p random g1 -n 1000

gui:
	java matchup.sim.Simulator -p random random --gui --fps 1
