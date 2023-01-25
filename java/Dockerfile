# syntax=docker/dockerfile:experimental                      
FROM maven:3.8.6-openjdk-11-slim as build

COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw mvn clean package spring-boot:repackage -DskipTests

FROM maven:3.8.6-openjdk-11-slim

COPY --from=build target/mct-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "mct-0.0.1-SNAPSHOT.jar"]
