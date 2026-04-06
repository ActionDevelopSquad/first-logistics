# 일등 물류 (First Logistics)

> MSA 기반 물류 관리 플랫폼입니다.
> Monorepo + Multi-Module 구조로 DDD 4계층 아키텍처를 적용하며,
> 주문 생성부터 배송 완료까지의 전체 물류 흐름을 관리합니다.
> 서비스 간 통신은 Kafka를 활용한 비동기 이벤트 메시징과 FeignClient 기반 동기 호출을 병행합니다.

---

## 목차

- [팀원 역할분담](#-팀원-역할분담)
- [기술 스택](#-기술-스택)
- [프로젝트 목적](#-프로젝트-목적)
- [아키텍처](#-아키텍처)
- [서비스 구성](#-서비스-구성)
- [ERD](#-erd)
- [실행 방법](#-실행-방법)
- [API 엔드포인트](#-api-엔드포인트)
- [기술적 의사결정](#-기술적-의사결정)
- [테스트](#-테스트)
- [브랜치 전략](#-브랜치-전략)

---

## 팀원 역할분담

| 팀원 | 담당 도메인 |
|------|------------|
| **공통 (다같이)** | common, common-jpa, common-event, common-security, common-swagger |
| **승원** | 업체, 상품(재고) |
| **진건** | 회원(인증, 권한), API Gateway(인증, 인가, 라우팅), common-security, Spring Cloud, Eureka |
| **현희** | 알림(슬랙), AI |
| **하은** | 주문 |
| **재빈** | 배송 |
| **세희** | 허브(경로) |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.x, Spring Cloud 2025.0.1 |
| **Build** | Gradle 9.2.0 (Multi-Module) |
| **Database** | PostgreSQL 16 (pgvector), Master/Slave 읽기-쓰기 분리 |
| **Cache** | Redis 7.2 |
| **Message Broker** | Apache Kafka (KRaft) |
| **Query** | QueryDSL 5.0 (커서 기반 페이지네이션) |
| **AI** | Spring AI (OpenAI, pgvector) |
| **Service Discovery** | Spring Cloud Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **인증/인가** | Spring Security + Keycloak (JWT) |
| **분산 추적** | Zipkin |
| **Infrastructure** | Docker, Docker Compose |
| **API 문서** | Swagger (SpringDoc OpenAPI) |
| **Testing** | JUnit 5, Mockito, H2 |

---

## 프로젝트 목적

일등 물류는 전국 17개 허브를 기반으로 한 **B2B 물류 배송 플랫폼**입니다.

- **주문 기반 배송 자동 생성**: 주문 승인 시 Kafka 이벤트를 통해 배송이 자동으로 생성되고, 허브 간 최적 경로가 계산됩니다.
- **배송 담당자 자동 배정**: 허브별 배송 담당자가 순번 기반으로 자동 배정되며, 분산락으로 동시성��� 제어합니다.
- **실시간 배송 상태 추적**: 배송 상태 변경 시 Kafka 이벤트를 통해 Slack 알림이 발송됩니다.
- **AI 연동**: OpenAI를 활용한 메시지 생성 및 pgvector 기반 유사도 검색을 지원합니다.
- **역할 기반 접근 제어**: MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER 4단계 권한 체계를 적용합니다.

---

## 아키텍처

```
                          [Client]
                             |
                        [API Gateway]  ── JWT 검증, 라우팅
                             |
                      [Eureka Server]  ── 서비스 디스커버리
                             |
        ┌────────┬───────┬───────┬────────┬────────┬────────┬────────┐
        |        |       |       |        |        |        |        |
     [User]  [Hub]  [Company] [Product] [Order] [Delivery] [Noti]  [AI]
        |        |       |       |        |        |        |        |
        └────────┴───────┴───────┴────┬───┴────────┴────────┴────────┘
                                      |
                    ┌─────────────────┼─────────────────┐
                    |                 |                 |
              [PostgreSQL]       [Kafka]          [Redis]
            Master / Slave     이벤트 브로커        캐시
```

### 서비스 간 통신

- **동기 (FeignClient)**: 서비스 간 데이터 조회 및 검증
- **비동기 (Kafka)**: 이벤트 기반 처리 (주문 생성 -> 배송 생성, 배송 상태 변경 -> 슬랙 알림)

### 데이터베이스 구조

하나의 PostgreSQL 인스턴스에서 논리적으로 분리합니다.

```
PostgreSQL (5432)
├── DB: first-logistics (Master - 쓰기)
│   ├── delivery 스키마
│   ├── orders 스키마
│   ├── hub 스키마
│   ├── company 스키마
│   ├── product 스키마
│   ├── users 스키마
│   ├── notification 스키마
│   └── ai 스키마
└── DB: first-logistics-slave (Slave - 읽기)
    └── (동일 스키마 구조, Logical Replication)
```

- `@Transactional` -> Master DB
- `@Transactional(readOnly = true)` -> Slave DB

---

## 서비스 구성

| 서비스 | 담당 도메인 | 포트 | 설명 |
|--------|------------|------|------|
| `eureka-server` | 서비스 디스커버리 | 19090 | 모든 마이크로서비스 위치 관리 |
| `api-gateway` | API Gateway | 80 | 외부 요청 라우팅 + 인증/인가 |
| `user-service` | 사용자, 인증 | 8080 | 회원가입(승인 기반), 로그인, JWT, Keycloak 연동 |
| `hub-service` | 허브, 허브간 이동정보 | 8083 | 17개 허브 관리, 허브 간 경로, Redis 캐싱 |
| `company-service` | 업체 | 8084 | 생산/수령 업체 관리 |
| `product-service` | 상품 | 8085 | 상품 및 재고 관리 |
| `order-service` | 주문 | 8082 | 주문 생성/취소, 재고 감소/복원, 배송 생성 트리거 |
| `delivery-service` | 배송, 배송경로, 배송담당자 | 8081 | 배송 전체 흐름 관리, 배송담당자 순번 배정 |
| `notification-service` | 슬랙 메시지 | 8086 | AI 연동 메시지 생성, Slack API 발송 |
| `ai-service` | AI | 8087 | OpenAI 기반 메시지 생성, pgvector 유사도 검색 |

### 공통 모듈

| 모듈 | 설명 |
|------|------|
| `common` | 예외 처리, 공통 ���답 포맷, ErrorCode/SuccessCode |
| `common-jpa` | BaseEntity, JPA Auditing, Master/Slave DataSource 라우팅 |
| `common-event` | Kafka Producer/Consumer 공통 설정, ConsistentHashPartitioner |
| `common-security` | @RequireRole/@OnlyMaster AOP, SecurityUtils, UserRole |
| `common-swagger` | Swagger 공통 설정 |

### Kafka 토픽

| 토픽 | Producer | Consumer | 설명 |
|------|----------|----------|------|
| `order.accepted` | order-service | delivery-service | 주문 승인 시 배송 자동 생성 |
| `order.cancelled` | order-service | delivery-service | 주문 취소 시 배송 취소 |
| `user.status.changed` | user-service | delivery-service | 사용자 승인 시 배송담당자 자동 생성 |
| `delivery.created` | delivery-service | notification-service | 배송 생성 알림 |
| `delivery.status.changed` | delivery-service | notification-service | 배송 상태 변경 알림 |

### 인프라

| 인프라 | 포트 | 설명 |
|--------|------|------|
| PostgreSQL (pgvector) | 5432 | Master + Slave (Logical Replication) |
| Redis | 6379 | 허브 정보 캐싱, 분산락 |
| Kafka (KRaft) | 9092 / 29092 | 이벤트 브로커 (내부/외부) |
| Kafka UI | 8989 | Kafka 토픽 모니터링 |
| Zipkin | 9411 | 분산 추적 |
| Keycloak | 3300 | OAuth2/OIDC 인증 서버 |

---

## 프로젝트 구조

<details>
<summary>펼쳐보기</summary>

```
first-logistics/                    # 루트 (Monorepo)
├── common/                         # 공통 모듈 (예외, 응답 포맷)
├── common-jpa/                     # JPA Auditing, Master/Slave 라우팅
├── common-event/                   # Kafka 공통 설정
├── common-security/                # 권한 AOP (@RequireRole)
├── common-swagger/                 # Swagger 공통 설정
├── eureka-server/                  # 서비스 디스커버리
├── api-gateway/                    # API Gateway
├── user-service/                   # 사용자 서비스
├── hub-service/                    # 허브 서비스
├── company-service/                # 업체 서비스
├── product-service/                # 상품 서비스
├── order-service/                  # 주문 서비스
├── delivery-service/               # 배송 서비스
├── notification-service/           # 알림 서비스
├── ai-service/                     # AI 서비스
├── docker/
│   └── postgres/init.sql           # DB + 스키마 초기화
├── http/                           # IntelliJ HTTP Client 테스트
│   ├── delivery-setup.http         # 배송 통합 테스트 셋업
│   ├── delivery.http               # 배송 API 테스트
│   └── http-client.env.json        # 환경변수
├── docker-compose.yml
├── .env                            # 환경변수 (Git 미포함)
└── .env.sample                     # 환경변수 템플릿
```

### 도메인 서비스 패키지 구조 (DDD 4계층)

```
{domain}-service/
└── src/main/java/com/firstlogistics/{domain}/
    ├── presentation/               # Controller, Request/Response DTO
    ├── application/                # Service (Command/Query 분리), Port
    ├── domain/                     # Entity, VO, Repository 인터페이스
    └── infrastructure/             # JPA 구현체, Kafka, FeignClient
```

</details>

---

## 실행 방법

### 사전 요구사항

- Java 21
- Docker 24+, Docker Compose v2+
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

`.env` 주요 항목은 `.env.sample`확인

### 3. Docker Compose 실행

```bash
# 전체 빌드 + 실행
docker compose up --build -d

# 상태 확인
docker compose ps

# 로그 확인
docker compose logs -f delivery-service

# 종료
docker compose down

# 종료 + 볼륨 삭제 (DB 초기화)
docker compose down -v
```

### 4. 접속 URL

| 서비스 | URL |
|--------|-----|
| API Gateway | http://localhost |
| Eureka Dashboard | http://localhost:19090 |
| Kafka UI | http://localhost:8989 |
| Zipkin | http://localhost:9411 |
| Keycloak | http://localhost:3300 |

#### Swagger UI

| 서비스 | URL |
|--------|-----|
| user-service | http://localhost:8080/swagger-ui/index.html |
| delivery-service | http://localhost:8081/swagger-ui/index.html |
| order-service | http://localhost:8082/swagger-ui/index.html |
| hub-service | http://localhost:8083/swagger-ui/index.html |
| company-service | http://localhost:8084/swagger-ui/index.html |
| product-service | http://localhost:8085/swagger-ui/index.html |
| notification-service | http://localhost:8086/swagger-ui/index.html |
| ai-service | http://localhost:8087/swagger-ui/index.html |

### 5. API 테스트 (IntelliJ HTTP Client)

`http/` 디렉토리의 `.http` 파일을 IntelliJ에서 순서대로 실행합니다.

1. `delivery-setup.http` - 사전 데이터 셋업 (회원가입, 허브/업체 생성, 주문 생성)
2. `delivery.http` - 배송 서비스 API 테스트

---

## API 엔드포인트

> 기본 API Gateway (`http://localhost`) 경유

| 서비스 | 주요 URL 패턴 | 설명 |
|--------|---------------|------|
| User | `/api/v1/users/**` | 회원가입, 로그인, 사용자 관리 |
| Hub | `/api/v1/hubs/**` | 허브 CRUD, 허브 간 이동정보 |
| Hub Connection | `/api/v1/hub-connections/**` | 허브 간 경로 관리 |
| Company | `/api/v1/companies/**` | 업체 CRUD |
| Product | `/api/v1/products/**` | 상품 및 재고 관리 |
| Order | `/api/v1/orders/**` | 주문 생성/취소/승인 |
| Delivery | `/api/v1/deliveries/**` | 배송 CRUD, 상태 변경 |
| Delivery Manager | `/api/v1/delivery-managers/**` | 배송 담당자 관리 |
| Notification | `/api/v1/notifications/**` | 슬랙 알림 |

### 전체 물류 흐름

```
1. 주문 생성 (order-service)
   └─> Kafka [order.created] ─> 재고 예약 (product-service)
   └─> Kafka [stock.reserved] ─> 재고 예약 성공 수신

2. 주문 승인 (order-service)
   └─> Kafka [order.accepted] ─> 배송 생성 (delivery-service) + 재고 차감 (product-service)

3. 배송 생성 (delivery-service)
   └─> 허브 간 최적 경로 조회 (hub-service, FeignClient)
   └─> 배송 담당자 자동 배정 (분산락)
   └─> Kafka [delivery.created] ─> 주문 완료 처리 (order-service)
                                └─> 배송 담당자 알림 (notification-service)

4. 배송 진행
   허브 배송 시작 (start)
   └─> 허브 도착 (arrive-hub)
   └─> 허브 입고 (receive-hub)
   └─> [경유지 반복]
   └─> 업체 배송 시작 (start-company)
   └─> 배송 완료 (complete)
   └─> 각 단계마다 Kafka [delivery.status.changed]

5. 알림 발송 (notification-service)
   └─> 배송 이벤트 수신
   └─> AI 메시지 생성 (ai-service)
   └─> Slack 알림 발송

※ 실패 시 Saga 보상: 배송 생성 실패 -> 주문 자동 취소, 배송 취소 실패 -> 주문 서비스 보상
```

---

## 기술적 의사결정

| 주제 | 요약 |
|------|------|
| **DDD 4계층 아키텍처** | 표현/응용/도메인/인프라 계층 분리로 비즈니스 로직 보호, 도메인 엔티티와 JPA 엔티티 분리 |
| **CQRS 패턴** | CommandService/QueryService 분리, Command는 도메인 엔티티, Query는 QueryDSL Projection 사용 |
| **이벤트 기반 비동기 통신** | 주문-배송 간 Kafka 이벤트로 결합도 최소화, at-least-once + 멱등성 보장 |
| **Saga 보상 트랜잭션** | 배송 생성 실패 시 `delivery.creation.failed` 이벤트로 주문 자동 취소 |
| **분산락 (Redisson)** | 배송 담당자 배정 시 동시성 제어, Facade 패턴으로 락 획득 후 서비스 호출 |
| **낙관적 락** | 배송/배송담당자 엔티티 @Version으로 동시 수정 ���돌 감지 |
| **Master/Slave 읽기-쓰기 분리** | common-jpa에서 RoutingDataSource로 readOnly 트랜잭션을 Slave로 라우팅 |
| **커서 기반 페이지네이션** | offset 방식의 데이터 누락 문제 해결, createdAt + id 복�� 커서 |
| **Strategy 패턴 권한 검증** | 역할별 PermissionStrategy로 데이터 레벨 접근 제어 |
| **Port-Adapter 패턴** | Application은 Port 인터페이스만 의존, FeignClient는 Infrastructure에서 Adapter로 구현 |

---

## 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 서비스 테스트
./gradlew :delivery-service:test

# 테스트 리포트
open delivery-service/build/reports/tests/test/index.html
```

### 테스트 전략

| 레이어 | 전략 | 도구 |
|--------|------|------|
| Service | Mockito + BDD 스타일 단위 테스트 | JUnit 5, Mockito, AssertJ |
| Integration | @SpringBootTest + H2 인메모리 DB | JUnit 5, H2 |
| E2E (수동) | IntelliJ HTTP Client로 실제 서버 대상 API 흐름 검증 | `.http` 파일 |

### IntelliJ HTTP Client

별도 툴 없이 IDE에서 API를 호출하고, `.http` 파일을 Git으로 팀 전체가 공유합니다.

- **연속 호출**: JavaScript로 이전 응답값을 다음 요청에 활용 (회원가입 -> 로그인 -> 주문 생성 -> 배송 조회)
- **환경 분리**: `http-client.env.json`으로 환경별 URL/토큰 관리

---

## 권한 체계

| 권한 | 설명 |
|------|------|
| `MASTER` | 마스터 관리자 - 모든 기능 접근 가능 |
| `HUB_MANAGER` | 허브 관리자 - 담당 허브의 업체/배송담당자 관리 |
| `DELIVERY_MANAGER` | 배송 담당자 - 허브 배송 또는 업체 배송 담당 |
| `COMPANY_MANAGER` | 업체 담당자 - 소속 업체/상품 관리 |

- Gateway에서 JWT 검증 후 사용자 정보를 헤더로 전달
- 각 서비스에서 `@RequireRole` AOP로 역할 검증
- `DeliveryPermissionValidator` Strategy 패턴으로 데이터 레벨 접근 제어

---

## 브랜치 전략

```
prod <- dev <- feat/{domain}/{layer}/{issue번호}-{설명}
예) feat/order/application/3-order-service-read
```

---

## 주의사항

- `.env` 파일은 절대 Git에 커밋하지 마세요 (`.gitignore`에 포함됨)
- 프로덕션 배포 시 `ddl-auto`를 `validate` 또는 `none`으로 변경하세요
- Keycloak 초기 기동에 1~2분 소요될 수 있습니다

---

## 라이선스

This project is for educational purposes.
