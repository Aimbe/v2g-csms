package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.OutboxEvent;
import com.charging.domain.enums.OutboxStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    Page<OutboxEvent> findAllByStatusAndRetryCountLessThanOrderByIdAsc(
            OutboxStatusEnum status,
            Integer maxRetryCount,
            Pageable pageable
    );
}
