# ==========================================
# Stage 1: Build Environment
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy only pom.xml to resolve and cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build production artifact, skipping tests during image assembly
RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 2: Execution Environment
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root system group and user for security isolation
RUN addgroup -S nexoragroup && adduser -S nexorauser -G nexoragroup

# Copy the built jar from the builder stage
COPY --from=builder /build/target/nexora-api-0.0.1-SNAPSHOT.jar app.jar

# Adjust file ownership to the application user
RUN chown nexorauser:nexoragroup app.jar

# Switch to the non-root user
USER nexorauser

EXPOSE 8080

# Configure JVM flags optimized for container resource limits
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
