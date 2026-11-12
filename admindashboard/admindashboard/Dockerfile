# Upgraded to Java 21 to match your team's Spring Boot configuration
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper, pom.xml, and source code directly
COPY .mvn .mvn
COPY mvnw ./
COPY pom.xml ./
COPY src ./src

# Give permission
RUN chmod +x mvnw

# Build app
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run app
CMD ["sh", "-c", "java -jar target/admindashboard-0.0.1-SNAPSHOT.jar --server.port=${PORT}"]
