## BUILD EXECUTABLE stage 1 ##
FROM openjdk:17-jdk AS builder
WORKDIR /usr/build
COPY src src
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw package -DskipTests

## EXECUTE APPLICATION stage 2 ##
FROM openjdk:17-jdk
WORKDIR /usr/app
COPY --from=builder /usr/build/target/objectstorage-api-0.0.1-SNAPSHOT.jar objectstorage-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","objectstorage-api.jar"]





