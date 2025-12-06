# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar file
COPY target/subscription-panel-0.0.1-SNAPSHOT.jar /app/subscription-panel-0.0.1-SNAPSHOT.jar

# Expose the port your API is running on
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "subscription-panel-0.0.1-SNAPSHOT.jar"]
