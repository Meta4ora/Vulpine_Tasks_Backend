# Этап 1: Сборка приложения
FROM gradle:8.7-jdk21 as builder
WORKDIR /app
# Копируем файлы для сборки
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle
COPY src src
# Собираем fat JAR
RUN gradle buildFatJar --no-daemon

# Этап 2: Финальный образ
FROM openjdk:21-slim
WORKDIR /app
# Копируем собранный JAR из предыдущего этапа
COPY --from=builder /app/build/libs/*-all.jar app.jar
# Указываем порт, который слушает приложение (должен совпадать в коде)
EXPOSE 8080
# Команда для запуска
ENTRYPOINT ["java", "-jar", "app.jar"]