# OCPP 2.0.1 충전 도메인 - Spring Framework 기반 학습 프로젝트

OCPP 2.0.1 표준 기반 전기차 충전 시스템을 Spring Framework 스타일로 구현한 JPA 학습 프로젝트입니다.

## 프로젝트 개요

이 프로젝트는 **OCPP 2.0.1 (Open Charge Point Protocol)** 표준을 따르는 충전 인프라 도메인 모델을 Spring Boot + JPA로 구현합니다.

### 기술 스택
- **Java 25**
- **Spring Boot 3.5.6**
- **Gradle 8.12**
- **JPA (Hibernate)**
- **Oracle / H2 Database**

## 프로젝트 구조
```
src/main/java/com/charging/
├── ChargingDomainApplication.java      # Spring Boot 메인 클래스
├── domain/
│   ├── entity/                          # JPA 엔티티
│   │   ├── BaseEntity.java             # 공통 필드 (created_at, updated_at)
│   │   ├── Station.java                # 충전소 (ChargingStation)
│   │   ├── Evse.java                   # EVSE (Electric Vehicle Supply Equipment)
│   │   ├── Connector.java              # 커넥터
│   │   ├── Transaction.java            # 충전 트랜잭션
│   │   ├── MeterValue.java             # 측정값
│   │   └── ChargingProfile.java        # 충전 프로파일 (스마트 충전)
│   ├── enums/                           # Enum 클래스
│   │   ├── ConnectorStatusEnum.java
│   │   ├── ChargingStateEnum.java
│   │   ├── TransactionEventEnum.java
│   │   ├── OperationalStatusEnum.java
│   │   ├── ChargingProfilePurposeEnum.java
│   │   ├── ChargingProfileKindEnum.java
│   │   └── MeasurandEnum.java
│   └── repository/                      # JPA Repository
│       ├── StationRepository.java
│       ├── EvseRepository.java
│       ├── ConnectorRepository.java
│       ├── TransactionRepository.java
│       ├── MeterValueRepository.java
│       └── ChargingProfileRepository.java
├── service/                             # 비즈니스 로직 레이어
│   └── TransactionService.java
├── controller/                          # REST API Controller
│   └── TransactionController.java
├── dto/                                 # 데이터 전송 객체
│   ├── request/
│   └── response/
└── exception/                           # 예외 처리
    ├── ChargingException.java
    ├── ResourceNotFoundException.java
    └── handler/
        └── GlobalExceptionHandler.java
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

## 주요 기능

### 1. 트랜잭션 관리
```java
// 트랜잭션 시작
POST /api/transactions/start
  ?evseId=1&stationId=ST-001&connectorId=1&idToken=RFID-12345

// 트랜잭션 종료
POST /api/transactions/{transactionId}/stop
  ?stopReason=Normal

// 충전 상태 업데이트
PATCH /api/transactions/{transactionId}/charging-state
  ?chargingState=CHARGING

// 활성 트랜잭션 조회
GET /api/transactions/active
  ?stationId=ST-001
```

### 2. JPA 학습 포인트

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

### 3. Spring Framework 패턴

#### 레이어드 아키텍처
```
Controller (REST API)
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터 액세스)
    ↓
Entity (도메인 모델)
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
| Service 레이어 | `TransactionService.java` |
| Controller 레이어 | `TransactionController.java` |
| 예외 처리 | `GlobalExceptionHandler.java` |

## OCPP 2.0.1 vs 1.6 주요 차이점

| 항목 | OCPP 1.6 | OCPP 2.0.1 |
|------|----------|------------|
| 충전 장비 | ChargePoint | EVSE |
| 계층 구조 | 2-tier (ChargePoint > Connector) | 3-tier (ChargingStation > EVSE > Connector) |
| 트랜잭션 | StartTransaction, StopTransaction | TransactionEvent (통합) |
| 디바이스 모델 | 고정된 설정 | Component/Variable 동적 모델 |
| 데이터 전송 | MeterValues | SampledValue (더 세분화) |

## 브랜치 전략

이 프로젝트는 `feature/ocpp-charging-domain` 브랜치에서 개발되었습니다.

브랜치 네이밍 규칙:
- `feature/<keyword>`: 기능 개발
- `fix/<issue>`: 버그 수정
- `hotfix/<urgent>`: 긴급 수정

자세한 내용은 `claude.md` 파일을 참고하세요.

## 향후 확장 계획

- [ ] DTO 클래스 구현 (Request/Response 분리)
- [ ] 나머지 Service 구현 (StationService, EvseService, ChargingProfileService)
- [ ] 나머지 Controller 구현
- [ ] 테스트 코드 작성 (JUnit 5, Mockito)
- [ ] API 문서화 (Swagger/OpenAPI)
- [ ] OCPP 2.0.1 메시지 처리 구현
- [ ] WebSocket 통신 구현
- [ ] 스마트 충전 알고리즘 구현

## 참고 자료

- [OCPP 2.0.1 Specification](https://openchargealliance.org/protocols/open-charge-point-protocol/)
- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate ORM 문서](https://hibernate.org/orm/documentation/)
- [JPA 스펙 문서](https://jakarta.ee/specifications/persistence/)

## 라이센스

학습 목적으로 자유롭게 사용 가능합니다.
