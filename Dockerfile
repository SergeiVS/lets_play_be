
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
LABEL authors="serge"


COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -DskipTests -Dspring.profiles.active=dev -e

FROM openjdk:21-ea-1-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/usr/local/openjdk-21
ENV JAVA_TOOL_OPTIONS="--enable-preview"
ENV SPRING_SERVER=0.0.0.0
EXPOSE 8080

RUN mkdir /app

COPY --from=build /usr/src/app/target/letsPlayBackend-0.0.1-SNAPSHOT.jar /app/letsplay.jar
ENTRYPOINT ["java", "-jar", "--enable-preview", "/app/letsplay.jar", "-Dspring.profiles.active=dev"]