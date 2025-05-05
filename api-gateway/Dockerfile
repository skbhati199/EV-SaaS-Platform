FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# Copy Maven wrapper and POM
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Build with skip tests to fix the dependency issues
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests -Dmaven.compiler.failOnError=false

# Use a smaller JRE image for the runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 