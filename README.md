# AI & MAS Project 

Warmup assignment code is provided as a playground for using the GitHub platform
codes for running the warmup environment.

The code for the project should be in the root folder ```.../aimasnoop/src/<name>.java etc.```

# How to compile and run from github:
Open a command promt and navigate to the location chosen for the project (cd .../aimasnoop)

To compile the warmup project go to .../aimasnoop/warmup
```
javac sampleclients/*.java
javac searchclient/*.java
```

to run the warmup program
```
java -jar server.jar -l levels/<level>.lvl -g 50 -c "java searchclient.SearchClient <algorithm>
```
example run:
```
java -jar server.jar -l levels/firefly.lvl -g 50 -c "java searchclient.SearchClient AStar"
```

Algorighms: "AStar", "WAStar", "Greedy", "DFS", BFS"
Levels:  crunch, custom, firefly, friendofbfs, friendofdfs, sad1, sad2

Remember to recompile every time you update the code from GitHub before you run it.

# Setup in IntelliJ IDEA
Create a new "JAR Application" run configuration with the following setup:

JAR path:
```
../AiMasNoOp/server.jar
```
Program arguments:
```
-l "../AiMasNoOp/WarmUp/levels/SAD1.lvl" -g 50 -c "java Supervisor"
```

Working directory:
```
../AiMasNoOp/out/production/AiMasNoOp
```
Finally setup the correct project structure in File -> Project Structure.
Ensure that only the "src" is marked as a "Sources" folder.