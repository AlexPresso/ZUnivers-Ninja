FROM amazoncorretto:22.0.0-alpine3.19

LABEL authors="alexpresso"

RUN mkdir -p /app
WORKDIR /app

ARG VERSION
COPY "target/zunivers-ninja-${VERSION}-exec.jar" app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
