# syntax=docker/dockerfile:1.7
FROM gradle:9.2.0-jdk21-alpine AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY gradle.properties .
COPY sample-service/build.gradle sample-service/build.gradle
COPY common/build.gradle common/build.gradle
COPY common-jpa/build.gradle common-jpa/build.gradle
COPY common-event/build.gradle common-event/build.gradle
COPY common-security/build.gradle common-security/build.gradle
COPY company-service/build.gradle company-service/build.gradle
COPY notification-service/build.gradle notification-service/build.gradle
COPY order-service/build.gradle order-service/build.gradle
COPY delivery-service/build.gradle delivery-service/build.gradle
COPY eureka-server/build.gradle eureka-server/build.gradle
COPY api-gateway/build.gradle api-gateway/build.gradle
COPY user-service/build.gradle user-service/build.gradle
COPY hub-service/build.gradle hub-service/build.gradle
COPY product-service/build.gradle product-service/build.gradle
COPY ai-service/build.gradle ai-service/build.gradle

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :user-service:dependencies --no-daemon

COPY common/src common/src
COPY common-jpa/src common-jpa/src
COPY common-event/src common-event/src
COPY common-security/src common-security/src
COPY user-service/src user-service/src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :user-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]