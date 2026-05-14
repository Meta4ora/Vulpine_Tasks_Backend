# Этап сборки с Java 23
FROM gradle:8.11-jdk23 AS builder

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean buildFatJar --no-daemon

# Финальный образ с Java 23
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
