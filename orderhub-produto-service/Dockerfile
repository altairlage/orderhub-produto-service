# Build do core
FROM maven:3.9.6-eclipse-temurin-21 AS core-builder
WORKDIR /usr/src/app

COPY ./orderhub-core/orderhub-core ./
# Instala a lib no repositório Maven local do container de build
# Em uma empresa, esta lib seria baixada de algum repositorio maven interno,
# como por exemplo Artifactory, Bytesafe...
RUN mvn clean install -DskipTests

# Build do microserviço
FROM maven:3.9.6-eclipse-temurin-21 AS service-builder
WORKDIR /usr/src/app
# Copia o repositório Maven local inteiro do estágio anterior com o 'orderhub-core.jar' compilado
COPY --from=core-builder /root/.m2 /root/.m2

COPY ./orderhub-produto-service/orderhub-produto-service ./
RUN mvn clean package -DskipTests

# Build da imagem final do microservice
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=service-builder /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]