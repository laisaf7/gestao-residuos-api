# ============================================================
# Dockerfile multi-stage - API de Gestao de Residuos e Reciclagem
# ============================================================

# ---------- Etapa 1: build da aplicacao com Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Baixa as dependencias antes de copiar o codigo-fonte (melhor uso de cache de camadas)
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---------- Etapa 2: imagem final somente com o runtime ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/target/gestao-residuos-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
