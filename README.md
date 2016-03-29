# AI & MAS Project 

Warmup assignment code is provided as a playground for using the GitHub platform
codes for running the warmup environment.

The code for the project should be in the root folder ```.../aimasnoop/main.java etc.```

# How to compile and run from github:
Open a command promt and navigate to the location chosen for the project (cd .../aimasnoop)

To compile the warmup project go to .../aimas/warmup
```
javac sampleclients/*.java
javac searchclient/*.java
```

to run the warmup program
```
java -jar server.jar -l levels/<level>.lvl -g 50 -c "java searchclient.Searchclient <algorithm>
```
example run:
```
java -jar server.jar -l levels/firefly.lvl -g 50 -c "java searchclient.Searchclient AStar"
```

Algorighms: "AStar", "WAStar", "Greedy", "DFS", BFS"
Levels:  crunch, custom, firefly, friendofbfs, friendofdfs, sad1, sad2

Remember to recompile every time you update the code from GitHub before you run it.
