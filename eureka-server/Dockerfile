# syntax=docker/dockerfile:1.7
FROM gradle:9.2.0-jdk21-alpine AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY gradle.properties .

# 모든 서브프로젝트의 build.gradle 복사 (Gradle이 프로젝트 디렉토리를 인식하도록)
COPY sample-service/build.gradle sample-service/
COPY common/build.gradle common/
COPY common-jpa/build.gradle common-jpa/
COPY common-event/build.gradle common-event/
COPY common-security/build.gradle common-security/
COPY common-swagger/build.gradle common-swagger/build.gradle
COPY company-service/build.gradle company-service/
COPY notification-service/build.gradle notification-service/
COPY order-service/build.gradle order-service/
COPY delivery-service/build.gradle delivery-service/
COPY eureka-server/build.gradle eureka-server/
COPY api-gateway/build.gradle api-gateway/
COPY user-service/build.gradle user-service/
COPY hub-service/build.gradle hub-service/
COPY product-service/build.gradle product-service/
COPY ai-service/build.gradle ai-service/

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :eureka-server:dependencies --no-daemon

COPY eureka-server/src eureka-server/src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :eureka-server:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]