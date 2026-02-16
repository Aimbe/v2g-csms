package com.charging.adapter.in.websocket;

import com.charging.adapter.in.websocket.model.OcppCall;
import com.charging.adapter.in.websocket.model.OcppCallError;
import com.charging.adapter.in.websocket.model.OcppCallResult;
import com.charging.adapter.in.websocket.model.OcppErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OcppMessageParser {

    private final ObjectMapper objectMapper;

    public OcppCall parseCall(String message) {
        try {
            JsonNode array = objectMapper.readTree(message);
            if (!array.isArray() || array.size() < 4) {
                throw new IllegalArgumentException("Invalid OCPP CALL message format");
            }

            int messageType = array.get(0).asInt();
            if (messageType != OcppCall.MESSAGE_TYPE) {
                throw new IllegalArgumentException("Expected CALL message type (2), got: " + messageType);
            }

            String messageId = array.get(1).asText();
            String action = array.get(2).asText();
            String payload = objectMapper.writeValueAsString(array.get(3));

            return new OcppCall(messageId, action, payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse OCPP message: " + message, e);
        }
    }

    public int getMessageType(String message) {
        try {
            JsonNode array = objectMapper.readTree(message);
            if (!array.isArray() || array.isEmpty()) {
                throw new IllegalArgumentException("Invalid OCPP message format");
            }
            return array.get(0).asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse OCPP message", e);
        }
    }

    public String serializeCallResult(OcppCallResult result) {
        try {
            JsonNode payloadNode = objectMapper.readTree(result.payload());
            return objectMapper.writeValueAsString(new Object[]{
                OcppCallResult.MESSAGE_TYPE,
                result.messageId(),
                payloadNode
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CALL_RESULT", e);
        }
    }

    public String serializeCallError(OcppCallError error) {
        try {
            return objectMapper.writeValueAsString(new Object[]{
                OcppCallError.MESSAGE_TYPE,
                error.messageId(),
                error.errorCode().getValue(),
                error.errorDescription(),
                error.errorDetails() != null ? objectMapper.readTree(error.errorDetails()) : objectMapper.createObjectNode()
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CALL_ERROR", e);
        }
    }

    public <T> T deserializePayload(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize payload to " + type.getSimpleName(), e);
        }
    }

    public String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }
}
