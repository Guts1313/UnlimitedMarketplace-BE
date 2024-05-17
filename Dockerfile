# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder

RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.5-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-*.zip && \
    rm /tmp/gradle-*.zip

ENV GRADLE_HOME=/opt/gradle/gradle-7.5
ENV PATH=${GRADLE_HOME}/bin:${PATH}
ENV JAVA_HOME=/usr/local/openjdk-17

WORKDIR /app
ENV HOST 0.0.0.0

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
COPY src/ src/

RUN gradle clean build -x test

# Stage 2: Create the runtime environment
FROM openjdk:17-jdk-slim

COPY --from=builder /app/build/libs/unlimitedmarketplace-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

