# Указываем базовый образ с поддержкой Java 17 для этапа сборки
FROM openjdk:17-jdk-slim as build

# Рабочуя директорию для сборки
WORKDIR /app

# Копия файла Maven wrapper (если используется)
COPY mvnw .
COPY .mvn .mvn

# Копия остальных файлов проекта
COPY pom.xml .
COPY src src

# Скачиваем зависимости и собираем проект
RUN ./mvnw clean package -DskipTests

# Указываем базовый образ с поддержкой Java 17 для финального контейнера
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный jar-файл из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Указываем порт, на котором работает приложение
EXPOSE 8080

# Устанавливаем переменные среды
ENV SPRING_APPLICATION_NAME=bot \
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tg-bot \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD= \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_LIQUIBASE_CHANGE_LOG=classpath:liquibase/changelog-master.yml \
    TELEGRAM_BOT_TOKEN= \
    TELEGRAM_BOT_USERNAME=

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
