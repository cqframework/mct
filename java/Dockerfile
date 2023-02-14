# syntax=docker/dockerfile:experimental                      
FROM maven:3.8.6-openjdk-11-slim as build

COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw mvn clean package spring-boot:repackage -DskipTests

ENTRYPOINT ["mvn", "spring-boot:run"]
