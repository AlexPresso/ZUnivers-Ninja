FROM openjdk:17-jdk-slim

LABEL authors="alexpresso"

RUN mkdir -p /app
WORKDIR /app

ARG VERSION
COPY "target/zunivers-ninja-${VERSION}.jar" app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
