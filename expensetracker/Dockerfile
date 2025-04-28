# === Build Stage ===
FROM maven:3.8.5-openjdk-17 AS build

# Set working directory for the build stage
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the entire project and build the application
COPY . ./
RUN mvn clean package -DskipTests

# === Runtime Stage ===
FROM openjdk:17-slim

# Create necessary directories for uploaded content
RUN mkdir -p \
    /app/uploads/userimages \
    /app/uploads/line-chart-image \
    /app/uploads/pie-chart-image \
    /app/uploads/line-chart-table-image \
    /app/uploads/pie-chart-table-image && \
    chmod -R 777 /app/uploads

# Set working directory for runtime stage
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/ExpenseTracker-0.0.1-SNAPSHOT.jar /app/ExpenseTracker.jar

# Copy the local `uploads` folder into the container's `/app/uploads` directory
COPY uploads/ /app/uploads/

# Expose port 8080 for the application
EXPOSE 8080

# Set entrypoint for the application, passing paths for image storage
ENTRYPOINT ["java", \
    "-Duser.images.path=/app/uploads/userimages", \
    "-Dchart.line.image.path=/app/uploads/line-chart-image", \
    "-Dchart.pie.image.path=/app/uploads/pie-chart-image", \
    "-Dchart.line.table.path=/app/uploads/line-chart-table-image", \
    "-Dchart.pie.table.path=/app/uploads/pie-chart-table-image", \
    "-jar", "/app/ExpenseTracker.jar"]
