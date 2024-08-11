# Используем официальный Maven образ как базовый для сборки
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл Maven конфигурации (pom.xml) и исходный код в контейнер
COPY pom.xml .
COPY src ./src

# Выполняем сборку приложения
RUN mvn clean package -DskipTests

# Используем официальный JDK 17 образ для запуска приложения
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл JAR из стадии сборки
COPY --from=build /app/target/SpringSecurityApp-0.0.1-SNAPSHOT.jar /app/application.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8080

# Определяем переменные окружения для конфигурации
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/security_app_db \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    JAVA_OPTS=""

# Определяем команду для запуска Spring Boot приложения
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.datasource.url=${SPRING_DATASOURCE_URL} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} -Dspring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO} -jar /app/application.jar"]
