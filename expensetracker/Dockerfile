# Build stage
FROM maven:3.8.5-openjdk-17 AS build

# Copy only pom.xml and dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code and build the JAR
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim

# Copy the built JAR from the build stage
COPY --from=build /target/ExpenseTracker-0.0.1-SNAPSHOT.jar /ExpenseTracker.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/ExpenseTracker.jar"]

