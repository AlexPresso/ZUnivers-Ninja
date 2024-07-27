FROM amazoncorretto:22.0.0-alpine3.19

LABEL authors="alexpresso"

RUN mkdir -p /app
WORKDIR /app

ARG VERSION
COPY "target/zunivers-ninja-${VERSION}-exec.jar" app.jar

ENV JAVA_OPTS="-Duser.timezone=Europe/Paris"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
