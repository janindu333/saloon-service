FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/saloon-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
