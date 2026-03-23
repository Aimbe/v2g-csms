package com.charging.adapter.in.websocket.support;

import java.lang.reflect.Parameter;

/**
 * OCPP 핸들러 메서드의 파라미터를 해석(resolve)하는 전략 인터페이스.
 * Spring MVC의 HandlerMethodArgumentResolver와 동일한 구조.
 *
 * <p>각 구현체는 특정 파라미터 유형을 지원하는지 판단하고(supportsParameter),
 * 지원하는 경우 실제 값을 생성(resolveArgument)한다.</p>
 *
 * <pre>
 * 해석 순서 (Composite에 등록된 순서):
 *   1. OcppSessionArgumentResolver   - OcppSession 타입 주입
 *   2. MessageIdArgumentResolver     - @MessageId String 주입
 *   3. StationIdArgumentResolver     - @StationId String 주입
 *   4. OcppPayloadArgumentResolver   - @OcppPayload 또는 나머지 DTO 역직렬화
 * </pre>
 */
public interface OcppHandlerMethodArgumentResolver {

    /**
     * 이 resolver가 주어진 파라미터를 해석할 수 있는지 판단한다.
     *
     * @param parameter 핸들러 메서드의 파라미터 정보
     * @return 해석 가능 여부
     */
    boolean supportsParameter(Parameter parameter);

    /**
     * 파라미터에 대한 실제 값을 해석(생성)한다.
     *
     * @param parameter 핸들러 메서드의 파라미터 정보
     * @param context   현재 OCPP 요청 컨텍스트
     * @return 해석된 파라미터 값
     */
    Object resolveArgument(Parameter parameter, OcppActionContext context);
}
