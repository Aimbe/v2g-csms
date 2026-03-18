package com.charging.adapter.out.persistence.adapter;

import com.charging.adapter.out.persistence.repository.OutboxEventRepository;
import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.enums.OutboxStatusEnum;
import com.charging.domain.port.out.OutboxEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventPersistenceAdapter implements OutboxEventPort {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit, int maxRetryCount) {
        return outboxEventRepository.findAllByStatusAndRetryCountLessThanOrderByIdAsc(
                OutboxStatusEnum.INIT,
                maxRetryCount,
                PageRequest.of(0, limit)
        ).getContent();
    }
}
