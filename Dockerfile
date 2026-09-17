# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire repository
COPY . .

# Build the application
RUN cd "YOA(Your own Assistance)" && mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
# Copy the built JAR file from the build stage using JSON array format to safely handle spaces
COPY --from=build ["/app/YOA(Your own Assistance)/target/taskmanager-0.0.1-SNAPSHOT.jar", "/app/taskmanager.jar"]

# Expose port 8080
EXPOSE 8080

# Create the data directory for the H2 database
RUN mkdir -p /data

# Start the application
CMD ["java", "-jar", "/app/taskmanager.jar"]
