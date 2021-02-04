# graph-traveler
Simple project to traverse a graph of connected nodes.

## Building
* gradle docker 

## Pushing to docker hub 
Latest
```bash 
gradle docker dockerTagLatest dockerPushLatest 
```
Version 
```bash 
gradle docker dockerTagMe dockerPushMe 
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

### Running with gradle Application plugin
https://docs.gradle.org/current/userguide/application_plugin.html

To see a help and usage message run the program with --help
```bash
gradle run --args="--help"
```

To run all test using the graph.csv input file and the --all flag
```bash 
gradle run --args="--input=graph.csv --all"
```



