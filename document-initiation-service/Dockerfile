FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/document-initiation-service-1.0-SNAPSHOT.jar /app/document-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "document-service.jar"]

