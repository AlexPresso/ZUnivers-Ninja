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


## Usage / Deployment
- [Kubernetes](#kubernetes)
- [Docker](#docker)
- [Manual deployment](#manual-deployment)

## Kubernetes
For Kubernetes clusters, there is a helm chart available in [helm.alexpresso.me](https://github.com/AlexPresso/helm.alexpresso.me), see default [values.yaml file](https://github.com/AlexPresso/helm.alexpresso.me/blob/main/charts/zunivers-ninja/values.yaml)

## Docker
- Download the `docker-compose.yml` file ([docker-compose.yml example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/docker-compose.yml))
- Set a Neo4J password in `docker-compose.yml` (i.e: `NEO4J_AUTH=neo4j/MyVeryStrongPassword`)
  - Don't forget to put the same password for zunivers-ninja's `NEO4J_PASSWORD`
- (Optional) Set a Discord Webhook endpoint if you want to receive advices directly inside a Discord Channel.
- Run `docker compose up -d`

## Manual deployment
- Setup a Neo4J database ([docker-image](https://hub.docker.com/_/neo4j))
- (Choose between A or B):
  - A. Clone the repo and build the jar with `mvn package`
  - B. Download the `zunivers-ninja-<version>-exec.jar` from the [package artifacts page](https://github.com/AlexPresso/ZUnivers-Ninja/packages/1071646) 
- Create a `/config` directory next to the built jar
- Create a `/config/application.yml` file ([config file example](https://github.com/AlexPresso/ZUnivers-Ninja/blob/main/src/main/resources/application.yml))
- Run the app (`java -jar zunivers-ninja-<version>-exec.jar` or `mvn spring-boot:run`)

## Making plugins
The app supports plugins, by loading every jar files in a `/plugins` directory.  
See the [zuninja-plugin-example](https://github.com/AlexPresso/ZUnivers-Ninja/tree/main/zuninja-plugin-example) project.
