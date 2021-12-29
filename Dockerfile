FROM openjdk:latest

WORKDIR /usr/src/server
COPY target/*.jar .

ENV SERVER_MAX_THREADS=15

ENTRYPOINT ["java", "-jar", "./server.jar"]