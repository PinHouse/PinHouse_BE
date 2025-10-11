package com.pinHouse.server.platform.diagnostic.explanation.domain.repository;

import com.pinHouse.server.platform.diagnostic.explanation.domain.entity.DiagnosisQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisQuestionJpaRepository extends JpaRepository<DiagnosisQuestion, Long> {

}
