FROM maven:3.8.6-openjdk-11-slim AS build

COPY mvnw .
COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

ENTRYPOINT ["mvn", "spring-boot:run"]
