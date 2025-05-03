FROM eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Set up an alternate entry point for SimpleHttpServer
COPY src/main/java/com/ev/roamingservice/SimpleHttpServer.java /app/SimpleHttpServer.java
RUN javac -d . /app/SimpleHttpServer.java

# Default to Spring Boot application, but allow SimpleHttpServer to be used with environment variable
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_MODE=springboot

# Create entrypoint script
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8088
ENTRYPOINT ["/app/entrypoint.sh"] 