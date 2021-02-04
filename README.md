# graph-traveler
Simple project to traverse a graph of connected nodes.

## Building
To build a local docker image run 
```bash 
./gradlew docker 
```

## Pushing to docker hub 
Latest
```bash 
./gradlew docker dockerTagLatest dockerPushLatest 
```
Version
```bash 
./gradlew docker dockerTagMe dockerPushMe 
```

## Running 

### Running with Docker
Assuming that the current directory holds a file named graph.csv containing a comma separated input graph

* Using bash as the shell 
```bash
docker run -v $(pwd)/graph.csv:/data/data.txt madpoptart/graph-traveler --input /data/data.txt --all
```
* Using fish as the shell
```bash
docker run -v (pwd)/graph.csv:/data/data.txt madpoptart/graph-traveler --input /data/data.txt --all
```

### Running with gradle application plugin
https://docs.gradle.org/current/userguide/application_plugin.html

To see a help and usage message run the program with --help
```bash
./gradlew run --args="--help"
```

To run all test using the graph.csv file use the --all flag and the --input flag
```bash 
./gradlew run --args="--input=graph.csv --all"
```


## Building
To build a runnable program using gradle and the application plugin run
```bash
./gradlew installDist
```
The build will be placed in 
```bash
build/install
```
and can be run with 
```bash
build/install/graph-traveler/bin/graph-traveler --input=graph.csv --all 
```


