package com.charging.adapter.out.persistence.repository;

import com.charging.domain.entity.DashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardProjectionRepository extends JpaRepository<DashboardProjection, String> {
}
