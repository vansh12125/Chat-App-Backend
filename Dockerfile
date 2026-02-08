# Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
