## BUILD EXECUTABLE stage 1 ##
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /usr/build
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

## EXECUTE APPLICATION stage 2 ##
FROM eclipse-temurin:17-jre
WORKDIR /usr/app
COPY --from=builder /usr/build/target/*.jar rest-api.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","rest-api.jar"]


