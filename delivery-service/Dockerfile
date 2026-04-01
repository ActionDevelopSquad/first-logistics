# syntax=docker/dockerfile:1
FROM gradle:9.2.0-jdk21-alpine AS build
WORKDIR /workspace

# 1. build 파일만 먼저 복사 (의존성 캐시 레이어)
COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY common/build.gradle common/build.gradle
COPY delivery-service/build.gradle delivery-service/build.gradle

# 2. 의존성만 다운로드 (build.gradle 변경 없으면 캐시 히트)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :delivery-service:dependencies --no-daemon

# 3. 소스 복사
COPY common/src common/src
COPY delivery-service/src delivery-service/src

# 4. 빌드 (Gradle 캐시 마운트로 증분 빌드)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :delivery-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/delivery-service/build/libs/delivery-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
