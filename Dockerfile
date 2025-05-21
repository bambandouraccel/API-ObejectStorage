## BUILD EXECUTABLE stage 1 ##
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install 

## EXECUTE APPLICATION stage 2 ##
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","app.jar"]


