# EDA + Kafka 적용 가이드

## 1. 현재 구조 짧은 분석

이 프로젝트는 이미 **이벤트 중심 흐름**에 가깝습니다.

- OCPP 이벤트 진입점: `TransactionEventHandler`, `MeterValuesHandler`, `NotifyEVChargingNeedsHandler`, `NotifyEVChargingScheduleHandler`
- 유스케이스 처리: `application/service/*`
- 참고: 현재 `MeterValuesHandler`, `NotifyEVChargingScheduleHandler`처럼 일부 흐름은 Handler에서 Out Port를 직접 호출합니다.
- 영속화: `domain/port/out/*` → `adapter/out/persistence/*`

즉, **충전소에서 들어오는 OCPP 메시지 자체가 도메인 이벤트의 원천**이므로 Kafka를 붙이기 좋은 구조입니다.

## 2. 어디에 Kafka를 붙이는 게 맞는가

권장 위치는 **Handler 직후가 아니라 상태 변경/영속화 완료 이후**입니다.

```text
OCPP Handler
  -> Application Service 또는 Out Port 호출
  -> DB 상태 변경 + Outbox 적재 (같은 트랜잭션)
  -> DB Commit
  -> Outbox Relay
  -> Kafka
```

이유:

- OCPP 응답은 빠르게 반환해야 함
- Kafka 장애가 충전소 ACK 지연으로 번지면 안 됨
- DB 상태와 이벤트 발행의 정합성을 맞추기 쉬움

## 3. 권장 이벤트/토픽

| 도메인 이벤트 | 발행 시점 | Kafka Topic Key |
|---|---|---|
| `TransactionStarted` | `startTransaction()` 완료 후 | `transactionId` |
| `TransactionStateUpdated` | `updateChargingState()` 완료 후 | `transactionId` |
| `TransactionEnded` | `stopTransaction()` 완료 후 | `transactionId` |
| `MeterValueRecorded` | meter value 저장 후 | `transactionId` 우선, 없으면 `stationId:evseId` |
| `ChargingNeedsReceived` | V2G charging needs 저장 후 | `stationId:evseId` |
| `ChargingScheduleReceived` | V2G schedule 저장 후 | `stationId:evseId` |

예시 토픽:

- `ocpp.transaction.v1`
- `ocpp.meter-value.v1`
- `ocpp.v2g.charging-needs.v1`
- `ocpp.v2g.charging-schedule.v1`

## 4. 운영 관점 권장사항

### 반드시 권장

- **Outbox 패턴** 적용
- 이벤트 payload에 `eventId`, `occurredAt`, `stationId`, `evseId` 포함
- `transactionId`는 트랜잭션 계열 이벤트에서만 필수로 두고, EVSE 스코프 이벤트에서는 선택 필드로 관리
- Consumer는 **at-least-once** 기준으로 멱등 처리

### 비권장

- Handler 내부에서 Kafka 직접 발행
- DB 저장보다 Kafka 발행을 먼저 수행
- OCPP 요청/응답 경로를 Kafka 가용성에 직접 의존하게 만드는 구조

## 5. 이 프로젝트에 기대할 수 있는 효과

- 실시간 알림/정산/분석 Consumer와 느슨한 결합
- 트랜잭션, 계량값, V2G 스케줄을 독립 Consumer로 확장 가능
- 향후 스마트 충전, 분석, 모니터링 기능을 Kafka Consumer 기반으로 분리 가능
- 현재 프론트는 REST 조회 중심이므로, UI 실시간화를 원하면 SSE/WebSocket 또는 read model 최적화가 추가로 필요

## 6. 가장 작은 도입 순서

1. `TransactionServiceImpl` 상태 변경 후 발행할 도메인 이벤트 정의
2. Outbox 테이블 추가
3. Outbox relay가 Kafka로 발행
4. `ChargingNeeds`까지 확장
5. `MeterValues`, `ChargingSchedule`은 서비스 계층 정리 후 확장

현재 `MeterValues`, `NotifyEVChargingSchedule` 흐름은 Handler에서 Out Port를 직접 호출하므로, 이 둘은 서비스 계층으로 먼저 정리한 뒤 같은 Outbox 규칙을 적용하는 편이 안전합니다.

현재 코드베이스 기준으로는 **트랜잭션 이벤트부터 Kafka 연동을 시작하는 것이 가장 안전하고 효과적**입니다.
