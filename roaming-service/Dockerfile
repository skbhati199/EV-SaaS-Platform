FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy and compile SimpleHttpServer
COPY src/main/java/com/ev/roamingservice/SimpleHttpServer.java /app/
RUN mkdir -p /app/com/ev/roamingservice
RUN javac -d . SimpleHttpServer.java

# Set environment variables with default values
ENV SERVER_PORT=8088
ENV OCPI_EXTERNAL_URL=http://localhost:8088
ENV OCPI_PARTY_ID=EVP
ENV OCPI_COUNTRY_CODE=US
ENV OCPI_ROLE=CPO

EXPOSE 8088

# Run the SimpleHttpServer
CMD ["java", "com.ev.roamingservice.SimpleHttpServer"]
