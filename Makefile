all:
	java matchup.sim.Simulator -p g6 random --gui --fps 1

compile:
	javac matchup/sim/*.java

clean:
	rm matchup/*/*.class

run:
	java matchup.sim.Simulator -p g6 random

gui:
	java matchup.sim.Simulator -p g6 random --gui --fps 1
