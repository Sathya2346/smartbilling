# ---------------------------
# 1) Build Stage (Maven)
# ---------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy entire project and build
COPY . .
RUN mvn -q clean package -DskipTests


# ---------------------------
# 2) Run Stage (JDK Runtime)
# ---------------------------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from Build Stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start the Spring Boot server
ENTRYPOINT ["java", "-jar", "app.jar"]
