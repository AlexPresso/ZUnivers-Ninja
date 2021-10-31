# ZUnivers-Ninja

## Getting started
- setup a Neo4J database ([docker-image](https://hub.docker.com/_/neo4j))
- clone the repo
- build the jar with `mvn package`
- create a `/config` directory next to the built jar
- create a `/config/application.yml` file ([config file example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/src/main/resources/application.example.yml))
- run the jar (`java -jar zunivers-ninja-<version>-SNAPSHOT-exec.jar`)

## Making plugins
The app supports plugins, by loading every jar files in a `/plugins` directory.  
See the [zuninja-plugin-example](https://github.com/AlexPresso/ZUnivers-Ninja/tree/main/plugins/zuninja-plugin-example) project.
