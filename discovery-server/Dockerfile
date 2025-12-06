# Use official JDK 17 image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy jar
COPY target/discovery-server-1.0-SNAPSHOT.jar /app/discovery-server.jar

# Expose the port (usually Eureka runs on 8761)
EXPOSE 8761

# Run the service
ENTRYPOINT ["java", "-jar", "discovery-server.jar"]

