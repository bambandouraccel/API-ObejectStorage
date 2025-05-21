FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le jar depuis le contexte (tu dois t'assurer qu'il est bien là)
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


