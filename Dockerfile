# Imagen base para Java 21
FROM eclipse-temurin:21-jdk-alpine

# Crear directorio para la app
WORKDIR /app

# Copiar el .jar generado al contenedor
COPY target/ms-subjects-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto (ajusta si tu app usa otro)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
