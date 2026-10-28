FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q package -DskipTests

FROM eclipse-temurin:21-jre

RUN groupadd --system app && useradd --system --gid app --no-create-home app

WORKDIR /app

COPY --from=build --chown=app:app /build/target/activation-api-*.jar app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -fs http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
