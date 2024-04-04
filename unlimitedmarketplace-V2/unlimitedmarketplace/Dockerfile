# Stage 1: Build the application with Java 17 JDK
FROM openjdk:17-jdk-slim as builder

# Install necessary packages to download and unzip Gradle
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.5-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-*.zip && \
    rm /tmp/gradle-*.zip

# Set Gradle in the environment path
ENV GRADLE_HOME=/opt/gradle/gradle-7.5
ENV PATH=${GRADLE_HOME}/bin:${PATH}
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Set the working directory inside the Docker image
WORKDIR /app

# Copy the project files into the Docker image
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
COPY src/ src/

# Build the application, skipping tests to speed up the build
RUN gradle build -x test

# Stage 2: Create the runtime environment using OpenJDK 17 JRE
FROM openjdk:17-jdk-slim

# Copy the built application jar from the build stage to the current stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
