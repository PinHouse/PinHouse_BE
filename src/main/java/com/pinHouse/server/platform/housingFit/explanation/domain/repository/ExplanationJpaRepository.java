package com.pinHouse.server.platform.housingFit.explanation.domain.repository;

import com.pinHouse.server.platform.housingFit.explanation.domain.entity.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplanationJpaRepository extends JpaRepository<Explanation, Long> {
}
