# === Build Stage ===
FROM maven:3.8.5-openjdk-17 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml to cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the rest of the project and build
COPY . ./
RUN mvn clean package -DskipTests

# === Runtime Stage ===
FROM openjdk:17-slim

# Create a writable folder for uploaded profile images
RUN mkdir -p /uploads/userimages && chmod -R 777 /uploads

# Set working directory
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/ExpenseTracker-0.0.1-SNAPSHOT.jar ExpenseTracker.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application and pass the image upload path as a system property
ENTRYPOINT ["java", "-Duser.images.path=/uploads/userimages", "-jar", "ExpenseTracker.jar"]


