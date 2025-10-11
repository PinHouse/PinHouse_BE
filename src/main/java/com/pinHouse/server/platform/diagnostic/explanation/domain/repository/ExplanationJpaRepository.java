package com.pinHouse.server.platform.diagnostic.explanation.domain.repository;

import com.pinHouse.server.platform.diagnostic.explanation.domain.entity.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplanationJpaRepository extends JpaRepository<Explanation, Long> {
}
