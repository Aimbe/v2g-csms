# OCPP 2.0.1 충전 도메인 - Hexagonal Architecture 학습 프로젝트

OCPP 2.0.1 표준 기반 전기차 충전 시스템을 **Hexagonal Architecture (육각형 아키텍처)** 패턴과 WebSocket 기반 실시간 메시지 처리로 구현한 Spring Boot 학습 프로젝트입니다.

## 프로젝트 개요

이 프로젝트는 **OCPP 2.0.1 (Open Charge Point Protocol)** 표준을 따르는 충전 인프라 도메인 모델을 구현하며, 다음 아키텍처 원칙을 실습합니다:

- **Hexagonal Architecture (Ports & Adapters)**: 도메인을 프로토콜과 기술 스택으로부터 분리
- **SOLID 원칙**: 의존성 역전, 단일 책임, 개방-폐쇄 원칙
- **Clean Architecture**: 계층 간 명확한 경계와 의존성 흐름
- **WebSocket 실시간 통신**: OCPP 2.0.1 JSON-RPC 2.0 메시지 처리
- **ArgumentResolver 패턴**: 동적 메시지 디스패칭과 타입 안전성

### 기술 스택
- **Java 25**
- **Spring Boot 3.5.6**
- **Spring WebSocket** (OCPP 메시지 실시간 처리)
- **Gradle 8.12**
- **JPA (Hibernate)**
- **Oracle / H2 Database**

## 프로젝트 구조 (Hexagonal Architecture)

```
src/main/java/com/charging/
│
├── ChargingDomainApplication.java          # Spring Boot 메인 클래스
│
├── domain/                                  # 도메인 계층 (비즈니스 로직, 기술 독립적)
│   ├── entity/
│   │   ├── BaseEntity.java                 # 공통 필드 (created_at, updated_at)
│   │   ├── Station.java                    # 충전소
│   │   ├── Evse.java                       # EVSE (Electric Vehicle Supply Equipment)
│   │   ├── Connector.java                  # 커넥터
│   │   ├── Transaction.java                # 충전 트랜잭션
│   │   ├── MeterValue.java                 # 측정값
│   │   └── ChargingProfile.java            # 충전 프로파일
│   │
│   ├── enums/                               # 도메인 Enum
│   │   ├── ConnectorStatusEnum.java
│   │   ├── ChargingStateEnum.java
│   │   ├── TransactionEventEnum.java
│   │   ├── OperationalStatusEnum.java
│   │   ├── ChargingProfilePurposeEnum.java
│   │   ├── ChargingProfileKindEnum.java
│   │   └── MeasurandEnum.java
│   │
│   ├── exception/                           # 도메인 예외
│   │   ├── ChargingException.java
│   │   └── ResourceNotFoundException.java
│   │
│   └── port/                                # Ports (비즈니스 인터페이스)
│       ├── in/                              # Input Ports (유스케이스)
│       │   ├── TransactionUseCase.java     # 트랜잭션 관리 유스케이스
│       │   └── BootNotificationUseCase.java # BootNotification 처리 유스케이스
│       │
│       └── out/                             # Output Ports (외부 시스템 인터페이스)
│           ├── StationPersistencePort.java
│           ├── EvsePersistencePort.java
│           ├── ConnectorPersistencePort.java
│           ├── TransactionPersistencePort.java
│           ├── MeterValuePersistencePort.java
│           └── ChargingProfilePersistencePort.java
│
├── application/                             # 애플리케이션 계층 (유스케이스 구현)
│   └── service/
│       ├── TransactionServiceImpl.java      # TransactionUseCase 구현
│       └── BootNotificationServiceImpl.java # BootNotificationUseCase 구현
│
└── adapter/                                 # Adapters (기술 구현)
    ├── in/
    │   ├── web/                             # REST API Adapter
    │   │   ├── TransactionController.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── dto/
    │   │       ├── request/
    │   │       └── response/
    │   │
    │   └── websocket/                       # WebSocket OCPP Adapter
    │       ├── OcppWebSocketHandler.java   # WebSocket 메시지 핸들러
    │       ├── OcppMessageParser.java      # JSON-RPC 2.0 파싱/직렬화
    │       ├── OcppActionDispatcher.java   # ArgumentResolver 기반 디스패쳐
    │       ├── OcppSessionManager.java     # 세션 관리
    │       ├── model/                       # OCPP 메시지 모델
    │       │   ├── OcppMessage.java
    │       │   ├── OcppCallRequest.java
    │       │   ├── OcppCallResult.java
    │       │   └── OcppCallError.java
    │       │
    │       └── action/                      # OCPP Action 핸들러
    │           ├── BootNotificationActionHandler.java
    │           ├── TransactionEventActionHandler.java
    │           ├── StatusNotificationActionHandler.java
    │           ├── MeterValuesActionHandler.java
    │           └── HeartbeatActionHandler.java
    │
    └── out/
        └── persistence/                     # JPA Adapter
            ├── repository/                  # JPA Repositories
            │   ├── StationRepository.java
            │   ├── EvseRepository.java
            │   ├── ConnectorRepository.java
            │   ├── TransactionRepository.java
            │   ├── MeterValueRepository.java
            │   └── ChargingProfileRepository.java
            │
            └── adapter/                     # Persistence Port 구현체
                ├── StationPersistenceAdapter.java
                ├── EvsePersistenceAdapter.java
                ├── ConnectorPersistenceAdapter.java
                ├── TransactionPersistenceAdapter.java
                ├── MeterValuePersistenceAdapter.java
                └── ChargingProfilePersistenceAdapter.java
```

### Hexagonal Architecture 핵심 개념

```
┌─────────────────────────────────────────────────────────────┐
│                        외부 세계                              │
│  (클라이언트, DB, 3rd-Party 서비스)                         │
└──────────────────┬────────────────────────────────────────┘
                   │
         ┌─────────┴─────────┐
         │    ADAPTER        │
    ┌────▼────┐         ┌───▼─────┐
    │ REST    │         │WebSocket│
    │(Port)   │         │ (Port)  │
    └────┬────┘         └───┬─────┘
         │                  │
         └─────────┬────────┘
                   │
    ┌──────────────▼──────────────┐
    │    APPLICATION SERVICE      │
    │  (유스케이스 구현)           │
    └──────────────┬──────────────┘
                   │
    ┌──────────────▼──────────────┐
    │      DOMAIN MODEL           │
    │  (비즈니스 로직)             │
    │  (기술 독립적)              │
    └──────────────┬──────────────┘
                   │
         ┌─────────┴─────────┐
         │    ADAPTER        │
    ┌────▼────┐         ┌───▼─────┐
    │  JPA    │         │ External│
    │(Port)   │         │Services │
    └────┘────┘         └────┘────┘
         │                  │
         └─────────┬────────┘
                   │
         ┌─────────▼─────────┐
         │   외부 시스템      │
         │  (데이터베이스)    │
         └───────────────────┘
```

## OCPP 2.0.1 표준 구현

### 3-tier 계층 구조

OCPP 2.0.1은 다음과 같은 3계층 구조를 정의합니다:

```
┌─────────────────────────────────────┐
│    ChargingStation (충전소)          │  ← 최상위: 전체 충전 시설
└─────────────────────────────────────┘
              ↓ 1:N
┌─────────────────────────────────────┐
│    EVSE (충전 장비)                  │  ← 중간: 개별 충전 세션 관리
└─────────────────────────────────────┘
              ↓ 1:N
┌─────────────────────────────────────┐
│    Connector (커넥터)                │  ← 최하위: 물리적 충전 포트
└─────────────────────────────────────┘
```

### 핵심 엔티티 설명

#### 1. Station (충전소)
- 충전소 전체를 나타내는 최상위 엔티티
- 전력 수용량, 가격 정책, 스마트충전 알고리즘 설정
- 여러 개의 EVSE를 소유

#### 2. EVSE (Electric Vehicle Supply Equipment)
- 하나의 충전 세션을 담당하는 충전 장비
- 운영 상태 (OPERATIVE / INOPERATIVE) 관리
- 여러 개의 커넥터를 가질 수 있음
- **OCPP 2.0.1의 핵심 개념**: ChargePoint → EVSE로 명칭 변경

#### 3. Connector (커넥터)
- 실제 물리적 충전 포트
- 상태 관리: AVAILABLE, OCCUPIED, RESERVED, UNAVAILABLE, FAULTED
- 최대/최소 허용 전력량 설정

#### 4. Transaction (트랜잭션)
- 충전 세션 정보
- **OCPP 2.0.1 변경사항**: StartTransaction/StopTransaction → TransactionEvent로 통합
- 이벤트 유형: STARTED, UPDATED, ENDED
- 충전 상태: CHARGING, SUSPENDED_EV, SUSPENDED_EVSE, IDLE

#### 5. MeterValue (측정값)
- 충전 중 수집되는 다양한 측정값
- Measurand: ENERGY, POWER, CURRENT, VOLTAGE, SOC 등
- 트랜잭션별로 시계열 데이터 수집

#### 6. ChargingProfile (충전 프로파일)
- 스마트 충전을 위한 전력 제어 프로파일
- 목적: CHARGE_POINT_MAX_PROFILE, TX_DEFAULT_PROFILE, TX_PROFILE
- 종류: ABSOLUTE, RECURRING, RELATIVE

## WebSocket OCPP 메시지 처리

### ArgumentResolver 패턴을 활용한 OCPP 액션 디스패칭

본 프로젝트는 **ArgumentResolver 패턴**을 사용하여 OCPP 액션(BootNotification, TransactionEvent 등)을 동적으로 디스패치합니다.

#### 메시지 흐름

```
WebSocket Message (JSON-RPC 2.0)
  ↓
OcppWebSocketHandler.handleTextMessage()
  ↓
OcppMessageParser.parse()
  • JSON → OcppCallRequest 역직렬화
  • messageId, action, payload 추출
  ↓
OcppActionDispatcher.dispatch(action, payload)
  • Step 1: action명으로 핸들러 메서드 조회
            (예: "BootNotification" → BootNotificationActionHandler.handle())
  • Step 2: 메서드의 파라미터 타입 추출
            (BootNotificationRequest, OcppSession)
  • Step 3: JSON payload → 해당 타입으로 역직렬화
  • Step 4: handler.method.invoke(bean, request, session)
  ↓
ActionHandler.handle(Request, OcppSession)
  ↓
ApplicationService (유스케이스 구현)
  ↓
Domain Logic
  ↓
Persistence Adapter
  ↓
Database
  ↓
응답 생성 및 직렬화
  ↓
OcppMessageParser.serializeCallResult()
  ↓
WebSocketSession.sendMessage()
  ↓
클라이언트
```

#### 주요 이점

1. **타입 안전성**: 런타임에 파라미터 타입을 자동 추론하고 검증
2. **유연성**: 새로운 OCPP 액션 추가 시 핸들러만 작성하면 자동 등록
3. **명확성**: 각 액션별 비즈니스 로직이 명확하게 분리
4. **테스트 용이성**: 각 핸들러를 독립적으로 테스트 가능

### WebSocket 엔드포인트

```
ws://localhost:8080/ws/ocpp/{stationId}
```

### 지원하는 OCPP 액션

| 액션 | 설명 | 요청 | 응답 |
|------|------|------|------|
| **BootNotification** | 충전소 부팅 알림 | BootNotificationRequest | BootNotificationResponse |
| **TransactionEvent** | 트랜잭션 이벤트 (시작, 진행, 종료) | TransactionEventRequest | TransactionEventResponse |
| **StatusNotification** | 커넥터/EVSE 상태 변경 알림 | StatusNotificationRequest | StatusNotificationResponse |
| **MeterValues** | 측정값 전송 | MeterValuesRequest | MeterValuesResponse |
| **Heartbeat** | 주기적 하트비트 | HeartbeatRequest | HeartbeatResponse |

### WebSocket OCPP 메시지 예시

#### BootNotification 요청 (클라이언트 → 서버)

```json
[
  2,
  "boot-notification-1",
  "BootNotification",
  {
    "chargingStation": {
      "model": "A100",
      "vendorName": "Vendor Inc"
    },
    "reason": "PowerUp"
  }
]
```

#### BootNotification 응답 (서버 → 클라이언트)

```json
[
  3,
  "boot-notification-1",
  {
    "currentTime": "2025-02-16T10:30:00Z",
    "interval": 300,
    "status": "Accepted"
  }
]
```

## 주요 기능

### 1. REST API를 통한 트랜잭션 관리

```bash
# 트랜잭션 시작
POST /api/transactions/start
  ?evseId=1&stationId=ST-001&connectorId=1&idToken=RFID-12345

# 트랜잭션 종료
POST /api/transactions/{transactionId}/stop
  ?stopReason=Normal

# 충전 상태 업데이트
PATCH /api/transactions/{transactionId}/charging-state
  ?chargingState=CHARGING

# 활성 트랜잭션 조회
GET /api/transactions/active
  ?stationId=ST-001
```

### 2. WebSocket을 통한 OCPP 메시지 처리

```javascript
// WebSocket 연결 (클라이언트 측 예시)
const socket = new WebSocket('ws://localhost:8080/ws/ocpp/ST-001');

// BootNotification 전송
socket.send(JSON.stringify([
  2,                      // MessageType: Call
  'boot-001',             // MessageId
  'BootNotification',     // Action
  {                       // Payload
    chargingStation: {
      model: 'A100',
      vendorName: 'Vendor Inc'
    },
    reason: 'PowerUp'
  }
]));

// 응답 수신
socket.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('응답:', message);
};
```

### 3. JPA 학습 포인트

#### 엔티티 매핑
- `@Entity`, `@Table`: 엔티티 및 테이블 매핑
- `@Column`: 컬럼 속성 (nullable, length, precision, scale)
- `@Id`, `@GeneratedValue`: 기본 키 및 자동 생성
- `@Enumerated`: Enum 타입 매핑

#### 연관관계 매핑
- `@OneToMany` / `@ManyToOne`: 1:N 양방향 관계
- `@JoinColumn`, `@JoinColumns`: 외래 키 매핑
- `cascade = CascadeType.ALL`: 영속성 전이
- `orphanRemoval = true`: 고아 객체 제거
- `fetch = FetchType.LAZY`: 지연 로딩

#### 상속 관계
- `@MappedSuperclass`: BaseEntity를 통한 공통 필드 관리
- `@CreationTimestamp`, `@UpdateTimestamp`: 자동 타임스탬프

#### 복합 유니크 키
- `@UniqueConstraint`: 테이블 레벨 복합 유니크 키
- EVSE: (evse_id, station_id)
- Connector: (evse_id, station_id, connector_id)

#### N+1 문제 해결
- `JOIN FETCH`를 사용한 페치 조인
- Repository 메서드에서 최적화된 쿼리 작성

### 4. Hexagonal Architecture 패턴

#### Port와 Adapter의 역할

**Input Ports (유스케이스 인터페이스)**
```java
public interface TransactionUseCase {
    TransactionResponse startTransaction(TransactionRequest request);
    TransactionResponse stopTransaction(Long transactionId);
}
```

**Output Ports (외부 시스템 인터페이스)**
```java
public interface TransactionPersistencePort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
}
```

**Adapter (기술 구현)**
```java
@Component
public class TransactionPersistenceAdapter implements TransactionPersistencePort {
    private final TransactionRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }
}
```

#### 의존성 역전의 원칙 (DIP)
- 도메인 계층은 어떤 외부 기술에도 의존하지 않음
- 모든 외부 의존성은 adapter를 통해 주입됨
- 도메인은 interface(port)에만 의존

#### Spring Framework 패턴

**Hexagonal Architecture 흐름**
```
HTTP/WebSocket Request
    ↓
Controller/Handler (Adapter In)
    ↓
Input Port (인터페이스)
    ↓
Application Service (유스케이스 구현)
    ↓
Domain Model (비즈니스 로직)
    ↓
Output Port (인터페이스)
    ↓
Persistence Adapter (JPA 구현)
    ↓
Database
    ↓
응답 생성 및 반환
```

#### 예외 처리
- 커스텀 예외: `ChargingException`, `ResourceNotFoundException`
- `@RestControllerAdvice`를 통한 전역 예외 처리
- HTTP 상태 코드 및 에러 응답 표준화

#### 트랜잭션 관리
- `@Transactional`: 선언적 트랜잭션 관리
- 읽기 전용 최적화: `@Transactional(readOnly = true)`

## 환경 설정

### 필수 요구사항
- Java 25
- Gradle 8.12 이상 (또는 포함된 Gradle Wrapper 사용)
- Oracle Database (또는 H2 Database for 테스트)

### 데이터베이스 설정

#### Oracle 사용 시
`src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:xe
    username: your_username
    password: your_password
```

#### H2 Database 사용 시 (개발/테스트)
개발 프로파일로 실행하면 H2 인메모리 데이터베이스를 사용합니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

H2 콘솔 접속: http://localhost:8080/h2-console

## 실행 방법

### 0. Gradle Wrapper 초기화 (최초 1회만)
프로젝트를 처음 받았을 때, Gradle Wrapper를 초기화해야 합니다:

```bash
gradle wrapper --gradle-version=8.12
```

### 1. 프로젝트 빌드
```bash
./gradlew clean build
```

### 2. 애플리케이션 실행
```bash
# 기본 실행 (Oracle)
./gradlew bootRun

# 개발 환경 실행 (H2)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 3. Oracle 스키마 생성
Oracle Database를 사용하는 경우, `src/main/resources/schema.sql` 파일의 DDL을 실행하세요.

```bash
sqlplus username/password@database < src/main/resources/schema.sql
```

### 4. WebSocket 테스트 (선택사항)
WebSocket OCPP 메시지를 테스트하려면 다음과 같이 실행합니다:

```bash
# 별도의 WebSocket 클라이언트 또는 테스트 도구 사용
# 예: wscat, Postman, 또는 브라우저 개발자 도구

wscat -c ws://localhost:8080/ws/ocpp/ST-001
```

## 학습 주제별 참고 파일

| 학습 주제 | 참고 파일 |
|---------|---------|
| 기본 엔티티 매핑 | `Station.java`, `Evse.java` |
| 연관관계 매핑 | `Evse.java`, `Connector.java`, `Transaction.java` |
| 복합 유니크 키 | `Evse.java`, `Connector.java` |
| 상속 관계 매핑 | `BaseEntity.java` |
| Enum 매핑 | `Connector.java`, `Transaction.java` |
| Repository 패턴 | `*Repository.java` |
| N+1 문제 해결 | `StationRepository.java`, `EvseRepository.java` |
| 양방향 관계 | `Station.java`, `Evse.java` |
| Service 레이어 | `*ServiceImpl.java` in application/service |
| Controller 레이어 | `TransactionController.java` in adapter/in/web |
| 예외 처리 | `GlobalExceptionHandler.java` |
| **Hexagonal Architecture** | `domain/port/`, `adapter/` directories |
| **WebSocket 처리** | `adapter/in/websocket/OcppWebSocketHandler.java` |
| **ArgumentResolver 패턴** | `adapter/in/websocket/OcppActionDispatcher.java` |
| **OCPP 액션 핸들링** | `adapter/in/websocket/action/*Handler.java` |

## OCPP 2.0.1 vs 1.6 주요 차이점

| 항목 | OCPP 1.6 | OCPP 2.0.1 |
|------|----------|------------|
| 충전 장비 | ChargePoint | EVSE |
| 계층 구조 | 2-tier (ChargePoint > Connector) | 3-tier (ChargingStation > EVSE > Connector) |
| 트랜잭션 | StartTransaction, StopTransaction | TransactionEvent (통합) |
| 디바이스 모델 | 고정된 설정 | Component/Variable 동적 모델 |
| 데이터 전송 | MeterValues | SampledValue (더 세분화) |
| 통신 프로토콜 | SOAP | JSON-RPC 2.0 over WebSocket |

## 브랜치 전략

이 프로젝트는 `feature/ocpp-charging-domain` 및 `feature/create-charging-domain` 브랜치에서 개발되었습니다.

브랜치 네이밍 규칙:
- `feature/<keyword>`: 기능 개발
- `fix/<issue>`: 버그 수정
- `hotfix/<urgent>`: 긴급 수정
- `release/<version>`: 릴리즈 준비

자세한 내용은 `CLAUDE.md` 파일을 참고하세요.

## 향후 확장 계획

- [x] 기본 엔티티 및 리포지토리 구현
- [x] TransactionService 구현
- [x] REST API Controller 구현
- [x] Hexagonal Architecture로 리팩토링
- [x] WebSocket OCPP 메시지 처리 구현
- [x] ArgumentResolver 기반 액션 디스패처 구현
- [x] OCPP 액션 핸들러 구현 (BootNotification, TransactionEvent, StatusNotification, MeterValues, Heartbeat)
- [ ] DTO 클래스 확장 (모든 OCPP 메시지 타입 지원)
- [ ] 나머지 Service 구현 (StationService, EvseService, ChargingProfileService)
- [ ] 테스트 코드 작성 (JUnit 5, Mockito)
- [ ] API 문서화 (Swagger/OpenAPI)
- [ ] 스마트 충전 알고리즘 구현
- [ ] 실시간 메시징 큐 통합 (RabbitMQ, Kafka)
- [ ] 모니터링 및 메트릭 수집 (Micrometer, Prometheus)

## 참고 자료

- [OCPP 2.0.1 Specification](https://openchargealliance.org/protocols/open-charge-point-protocol/)
- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring WebSocket 공식 문서](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [Hexagonal Architecture (육각형 아키텍처) - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Hibernate ORM 문서](https://hibernate.org/orm/documentation/)
- [JPA 스펙 문서](https://jakarta.ee/specifications/persistence/)

## 라이센스

학습 목적으로 자유롭게 사용 가능합니다.
