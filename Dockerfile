
FROM maven:3.9.12-eclipse-temurin-25-alpine AS build
LABEL authors="serge"


COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -Dtests.skip=true -Dspring.profiles.active=dev -e

FROM openjdk:25-ea-25-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/usr/local/openjdk-25
ENV SPRING_SERVER=0.0.0.0
EXPOSE 8080

RUN mkdir /app

COPY --from=build /usr/src/app/target/letsPlayBackend-0.0.1-SNAPSHOT.jar /app/letsplay.jar
ENTRYPOINT ["java", "-jar", "/app/letsplay.jar", "-Dspring.profiles.active=dev"]