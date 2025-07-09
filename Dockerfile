# 🔹 Etapa 1: Build con Maven usando Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia pom.xml y descarga dependencias (acelera builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el código fuente y compila
COPY src ./src
RUN mvn clean package -DskipTests

# 🔹 Etapa 2: Imagen final mínima con JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia solo el JAR compilado
COPY --from=builder /app/target/ms-subjects-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usa tu microservicio
EXPOSE 8082

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
