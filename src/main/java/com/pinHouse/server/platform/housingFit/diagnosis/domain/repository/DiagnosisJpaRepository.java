package com.pinHouse.server.platform.housingFit.diagnosis.domain.repository;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Long> {

}
