package com.pinHouse.server.platform.housingFit.diagnosis.domain.repository;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisQuestionJpaRepository extends JpaRepository<DiagnosisQuestion, Long> {

}
