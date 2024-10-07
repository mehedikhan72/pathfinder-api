# Build stage
FROM openjdk:17-jdk-slim AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package -DskipTests -Dcheckstyle.skip=true

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
rm .env
COPY .env.test2 .env
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
