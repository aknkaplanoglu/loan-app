FROM openjdk:17-jdk-slim
COPY target/loan-service-1.0.0.jar loan-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loan-app.jar"]
