## BUILD EXECUTABLE stage 1 ##
FROM openjdk:17-jdk AS builder
WORKDIR /usr/build
COPY . .
#COPY src src
#COPY .mvn .mvn
#COPY mvnw .
#COPY pom.xml .
#RUN chmod +x mvnw
RUN mvn clean package -DskipTests

## EXECUTE APPLICATION stage 2 ##
FROM openjdk:17
WORKDIR /usr/app
COPY --from=builder /usr/build/target/*.jar rest-api.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","rest-api.jar"]


