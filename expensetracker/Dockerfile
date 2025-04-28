# === Build Stage ===
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY . ./
RUN mvn clean package -DskipTests

# === Runtime Stage ===
FROM openjdk:17-slim

# Create writable folders for uploaded content
RUN mkdir -p \
    /uploads/userimages \
    /uploads/line-chart-image \
    /uploads/pie-chart-image \
    /uploads/line-chart-table-image \
    /uploads/pie-chart-table-image && \
    chmod -R 777 /uploads

WORKDIR /app

COPY --from=build /app/target/ExpenseTracker-0.0.1-SNAPSHOT.jar ExpenseTracker.jar

COPY uploads/ /uploads/

EXPOSE 8080

# Pass all image/chart paths to the application
ENTRYPOINT ["java", "-Duser.images.path=/uploads/userimages", "-Dchart.line.image.path=/uploads/line-chart-image", "-Dchart.pie.image.path=/uploads/pie-chart-image", "-Dchart.line.table.path=/uploads/line-chart-table-image", "-Dchart.pie.table.path=/uploads/pie-chart-table-image", "-jar", "ExpenseTracker.jar"]

