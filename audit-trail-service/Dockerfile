FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/audit-trail-service-1.0-SNAPSHOT.jar /app/audit-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "audit-service.jar"]

