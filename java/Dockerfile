FROM maven:3.8.6-openjdk-11-slim AS build

COPY mvnw .
COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

FROM adoptopenjdk:11-jre-hotspot
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar