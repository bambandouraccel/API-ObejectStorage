## BUILD EXECUTABLE stage 1 ##
FROM maven:3.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
#RUN chmod +x mvnw
RUN mvn clean package -DskipTests

## EXECUTE APPLICATION stage 2 ##
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","rest-api.jar"]


