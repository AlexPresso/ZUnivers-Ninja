<div align="center">
<img src="https://repository-images.githubusercontent.com/420819440/51db7016-b325-4d1b-a1d3-ce82d771f58b" height="320px">
</div>

This project aims to suggest to one (or more) users: the best actions scenario to take.
It's currently based on a recursive method which always tries projections until it cannot do anything more than the previous iteration. 

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
- ✅ evolution projection
- ✅ constellation projection
- ✅ plugin system
- (Soon) auto subscription based on a rentability check


## Getting started
- setup a Neo4J database ([docker-image](https://hub.docker.com/_/neo4j))
- (choose between A or B):
  - A. clone the repo and build the jar with `mvn package`
  - B. download the `zunivers-ninja-<version>-exec.jar` from the [package artifacts page](https://github.com/AlexPresso/ZUnivers-Ninja/packages/1071646) 
- create a `/config` directory next to the built jar
- create a `/config/application.yml` file ([config file example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/src/main/resources/application.yml))
- run the jar (`java -jar zunivers-ninja-<version>-exec.jar` or `mvn spring-boot:run`)
## Docker Compose
- Download the `docker-compose.yml` file ([docker-compose.yml example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/docker-compose.yml))
- Create a `/config` directory next to the docker-compose file
- Create a `/config/application.yml` file ([config file example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/src/main/resources/application.yml))
- Modify the neo4j uri to `bolt://neo4j:7687` and set the webhook URL in `application.yml`
- run `docker compose up`

## Making plugins
The app supports plugins, by loading every jar files in a `/plugins` directory.  
See the [zuninja-plugin-example](https://github.com/AlexPresso/ZUnivers-Ninja/tree/main/zuninja-plugin-example) project.
