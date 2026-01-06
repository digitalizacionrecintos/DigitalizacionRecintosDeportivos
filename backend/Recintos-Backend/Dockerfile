FROM gradle:7.6-jdk17 AS builder

WORKDIR /build

COPY build.gradle settings.gradle ./

COPY src ./src

RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
