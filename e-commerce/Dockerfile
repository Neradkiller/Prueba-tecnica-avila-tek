# Fase 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /workspace/app

# Copiar solo el POM primero (para cachear dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación - OMITIR COMPLETAMENTE LOS TESTS
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -DskipITs

# Fase 2: Ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Copiar JAR desde la fase de construcción
COPY --from=builder /workspace/app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]