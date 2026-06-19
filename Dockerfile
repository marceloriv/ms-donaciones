# Etapa 1: compilar con Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Descarga dependencias primero (cache de Docker)
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline
COPY src src

# Empaqueta saltando las pruebas unitarias
RUN ./mvnw -q package -DskipTests

# Etapa 2: imagen final liviana
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
