FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# Copy source code
COPY pom.xml .
COPY src src

# Package the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Set timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the JAR file from the build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Set permissions
RUN chown -R appuser:appgroup /app
USER appuser

# Environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS=""

# Expose the application port
EXPOSE 8761

# Run the application with specific JVM options for containerization
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar"] 