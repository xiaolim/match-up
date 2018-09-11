all:
	java matchup.sim.Simulator -p random random --gui --fps 1

compile:
	javac matchup/sim/*.java

clean:
	rm matchup/*/*.class

run:
	java matchup.sim.Simulator -p random two_large_two_small_vary_risk

gui:
	java matchup.sim.Simulator -p random random --gui --fps 1
