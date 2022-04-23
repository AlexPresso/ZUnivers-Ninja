<div align="center">
<img src="https://repository-images.githubusercontent.com/420819440/51db7016-b325-4d1b-a1d3-ce82d771f58b" height="320px">
</div>

This project aims to suggest one (or more) users, the best actions scenario to take, while never losing points on the ZUnivers card game.
It's currently based on a recursive method which always try projections until it cannot do anything more than the previous iteration. 

I'm planning to move the projection system to a tree calculation system, generating all possible scenarios and returning the one providing the best score. 

## Disclaimer

This project is not affiliated with the ZUnivers's project. It's a community project.

## Features
- ✅ `!journa` projection
- ✅ `!recycle` projection
- ✅ `!fusion` projection
- ✅ `!upgrade` projection
- ✅ `!im` projection (with current event priority)
- ✅ `!craft` projection
- ✅ `!ascension` projection
- ✅ challenges projection
- ✅ plugin system
- (Soon) auto subscription based on a rentability check


## Getting started
- setup a Neo4J database ([docker-image](https://hub.docker.com/_/neo4j))
- clone the repo
- build the jar with `mvn package`
- create a `/config` directory next to the built jar
- create a `/config/application.yml` file ([config file example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/src/main/resources/application.example.yml))
- run the jar (`java -jar zunivers-ninja-<version>-SNAPSHOT-exec.jar` or `mvn spring-boot:run`)

## Making plugins
The app supports plugins, by loading every jar files in a `/plugins` directory.  
See the [zuninja-plugin-example](https://github.com/AlexPresso/ZUnivers-Ninja/tree/main/zuninja-plugin-example) project.
