# first-logistics (일등 물류)

MSA 구조의 물류 백엔드 시스템 샘플 프로젝트.
Monorepo + Multi-Module 기반으로 DDD 4계층 아키텍처를 적용한다.

---

## 기술 스택

| 영역 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.11 |
| Build | Gradle 9.2.0 (Multi-Module) |
| DB | PostgreSQL 16 |
| Cache | Redis 7.2 |
| Message Broker | Kafka, RabbitMQ |
| Infra | Docker, Docker Compose |

---

## 프로젝트 구조

```
first-logistics/               # 루트 (Monorepo)
├── common/                    # 공통 모듈 (예외, 응답 포맷)
│   └── src/main/java/common/
│       ├── exception/         # BaseException, GlobalExceptionHandler
│       └── response/          # ApiResponse, ErrorCode, SuccessCode
├── sample-service/            # 샘플 서비스 (DDD 구조 참고용)
│   └── src/main/java/com/project/sampleservice/
│       ├── presentation/      # Controller, Request/Response DTO
│       ├── application/       # CommandService, QueryService, Command
│       ├── domain/            # Entity, Repository 인터페이스, Exception
│       └── infrastructure/    # JPA 구현체
├── docker/
│   └── postgres/init.sql      # 스키마 초기화
├── http/
│   └── item.http              # API 테스트 (IntelliJ HTTP Client)
├── docker-compose.yml
├── .env                       # 실제 환경변수 (Git 미포함)
└── .env.sample                # 환경변수 템플릿
```

---

## 실행 방법

### 사전 준비

- Java 21
- Docker, Docker Compose
- Git

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd first-logistics
```

### 2. 환경변수 설정

```bash
cp .env.sample .env
```

`.env`를 열고 아래 항목을 채운다. 나머지는 기본값으로 동작한다.

```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=
POSTGRES_DB=postgres
POSTGRES_DB_URL=jdbc:postgresql://postgres:5432/postgres
```

### 3. 전체 빌드

루트에서 전체 모듈을 빌드한다.

```bash
./gradlew clean build
```

특정 모듈만 빌드할 경우:

```bash
./gradlew :sample-service:build
```

테스트 없이 실행 가능한 jar만 생성할 경우:

```bash
./gradlew :sample-service:bootJar -x test
```

### 4. Docker Compose 실행

```bash
# 컨테이너 + 볼륨 초기화 후 새로 실행
docker compose down -v
docker compose up --build -d

# 상태 확인
docker ps

# sample-service 로그 확인
docker compose logs -f sample-service
```

실행되는 컨테이너 목록:

| 컨테이너 | 포트 |
|----------|------|
| PostgreSQL | 5432 |
| Redis | 6379 |
| Zookeeper | 2181 |
| Kafka (내부) | 9092 |
| Kafka (외부/호스트) | 29092 |
| Kafka UI | 8989 |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |
| sample-service | 8080 |

### 5. API 테스트

IntelliJ에서 `http/item.http` 파일을 열고 순서대로 실행한다.

```
POST   /api/items       아이템 생성
GET    /api/items       전체 조회
GET    /api/items/{id}  단건 조회
PUT    /api/items/{id}  수정
DELETE /api/items/{id}  삭제
```

Kafka UI: http://localhost:8989
RabbitMQ Management: http://localhost:15672 (guest / guest)

---

## 로컬 실행 (IntelliJ, Docker 없이)

`SampleServiceApplication`을 직접 실행할 때는 H2 인메모리 DB를 사용한다.

**VM options 또는 Environment variables:**
```
spring.profiles.active=local
```

> `local` 프로필은 H2를 사용하므로 Docker 없이 바로 실행 가능하다.

H2 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:sampledb`
- Username: `sa`

---

## 테스트 실행

```bash
# 전체 테스트
./gradlew test

# sample-service 테스트만
./gradlew :sample-service:test
```

테스트는 H2 인메모리 DB를 사용하므로 Docker 없이 실행 가능하다.

---

## 공통 응답 포맷

**성공**
```json
{
  "code": "COMMON_S001",
  "status": "OK",
  "message": "성공하였습니다.",
  "data": { ... },
  "timestamp": "2026-03-24T10:00:00"
}
```

**실패**
```json
{
  "code": "ITEM_001",
  "status": "NOT_FOUND",
  "message": "아이템을 찾을 수 없습니다.",
  "data": null,
  "timestamp": "2026-03-24T10:00:00"
}
```

```
테스트 절차

  Step 1. 전체 프로젝트 빌드 (루트 기준)

  cd D:/project/first-logistics

  # 전체 clean build (common + sample-service 포함)
  ./gradlew clean build

  Step 2. sample-service 단독 빌드

  # 테스트 포함 빌드
  ./gradlew :sample-service:build

  # 테스트 제외 bootJar만
  ./gradlew :sample-service:bootJar -x test

  Step 3. 테스트만 실행

  # sample-service 테스트
  ./gradlew :sample-service:test

  # 결과 확인
  open sample-service/build/reports/tests/test/index.html

  Step 4. Docker 컨테이너 초기화 후 빌드

  # 컨테이너 + 볼륨 전체 삭제
  docker compose down -v

  # 이미지 강제 재빌드 + 백그라운드 실행
  docker compose up --build -d

  # 상태 확인
  docker ps

  # sample-service 로그 확인
  docker compose logs -f sample-service

```

---

## 브랜치 전략

```
prod ← dev ← feat/{domain}/{layer}/{issue번호}-{설명}
예) feat/order/application/3-order-service-read
```
