# Kafka Learning Server 모듈

## 목적

`kafka-learning-server` 는 기존 `csms-app` 의 Outbox/Kafka 흐름과 별도로,
Kafka producer / consumer 를 직접 다뤄보기 위한 학습용 Spring Boot 모듈입니다.

즉, 이 모듈은:

- Kafka broker에 직접 연결되는 client 서버 역할을 하고
- REST 요청으로 메시지를 발행할 수 있으며
- Kafka에서 소비한 메시지를 바로 확인할 수 있습니다.

## 핵심 포인트

- 모듈명: `kafka-learning-server`
- 기본 포트: `8081`
- 기본 broker: `localhost:9092`
- 기본 topic:
  - `learning.demo.v1`
  - `learning.demo.retry.v1`

## 제공 API

### 1. Topic 목록 조회

`GET /api/learning/kafka/topics`

응답 예시:

```json
{
  "defaultTopic": "learning.demo.v1",
  "topics": [
    "learning.demo.v1",
    "learning.demo.retry.v1"
  ]
}
```

### 2. 메시지 발행

`POST /api/learning/kafka/messages`

요청 예시:

```json
{
  "topic": "learning.demo.v1",
  "key": "station-001",
  "payload": "hello kafka"
}
```

응답 예시:

```json
{
  "topic": "learning.demo.v1",
  "partition": 0,
  "offset": 12,
  "key": "station-001",
  "payload": "hello kafka"
}
```

### 3. 최근 소비 메시지 조회

`GET /api/learning/kafka/messages?limit=20`

## 실행 방법

```bash
docker compose up -d kafka
./gradlew :kafka-learning-server:bootRun
```

## 외부 클라이언트 실습 예시

### console producer

```bash
kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic learning.demo.v1
```

### console consumer

```bash
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic learning.demo.v1 \
  --from-beginning
```

## 추천 학습 순서

1. `POST /api/learning/kafka/messages` 로 서버에서 발행
2. console consumer 또는 `kcat` 으로 broker에서 메시지 읽기
3. console producer 로 broker에 직접 쓰기
4. `GET /api/learning/kafka/messages` 로 서버 consumer 수신 내역 확인
5. topic / key / consumer group 을 바꿔보며 동작 차이 관찰
