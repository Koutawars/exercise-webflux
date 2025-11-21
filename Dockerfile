FROM eclipse-temurin:21-jdk-alpine
COPY applications/app-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]