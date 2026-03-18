package com.charging.domain.port.out;

import com.charging.domain.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventPort {
    OutboxEvent save(OutboxEvent outboxEvent);
    List<OutboxEvent> findPendingEvents(int limit, int maxRetryCount);
}
