all:
	java matchup.sim.Simulator -p random random --gui --fps 1

compile:
	javac matchup/sim/*.java

clean:
	rm matchup/*/*.class

run:
	java matchup.sim.Simulator -p random random --gui --fps 1
