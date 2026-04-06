# syntax=docker/dockerfile:1
FROM gradle:9.2.0-jdk21-alpine AS build
WORKDIR /workspace

# 1. 빌드 설정 파일 및 래퍼 복사 (의존성 캐싱)
COPY gradlew .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
COPY gradle/ gradle/
COPY settings.gradle .
COPY gradle.properties .
COPY build.gradle .

# 모듈별 build.gradle 복사 (order-service와 의존하는 공통 모듈들)
COPY common/build.gradle common/build.gradle
COPY common-jpa/build.gradle common-jpa/build.gradle
COPY common-event/build.gradle common-event/build.gradle
COPY common-security/build.gradle common-security/build.gradle
COPY common-swagger/build.gradle common-swagger/build.gradle
COPY order-service/build.gradle order-service/build.gradle

# settings.gradle에 포함된 다른 서비스들의 디렉토리 구조만 생성 (빌드 오류 방지)
RUN mkdir -p delivery-service sample-service common-event company-service \
    notification-service eureka-server api-gateway user-service hub-service product-service ai-service

# 2. 의존성 다운로드 (캐시 활용)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :order-service:dependencies --no-daemon

# 3. 실제 소스 코드 복사
COPY common/src common/src
COPY common-jpa/src common-jpa/src
COPY common-event/src common-event/src
COPY common-security/src common-security/src
COPY common-swagger/src common-swagger/src
COPY order-service/src order-service/src

# 4. jar 파일 빌드
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :order-service:bootJar --no-daemon -x test

# --- 실행 단계 ---
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app

# 보안을 위한 비관리자 계정 생성
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# 빌드 스테이지에서 생성된 jar 복사 (파일명 확인 필수!)
COPY --from=build /workspace/order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]