package com.pinHouse.server.platform.adapter.out;

import com.pinHouse.server.platform.adapter.out.jpa.diagnosis.DiagnosisJpaEntity;
import com.pinHouse.server.platform.adapter.out.jpa.diagnosis.DiagnosisJpaRepository;
import com.pinHouse.server.platform.housingFit.diagnosis.application.usecase.DiagnosisPort;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiagnosisJpaAdapter implements DiagnosisPort {

    private final DiagnosisJpaRepository repository;

    @Override
    public Diagnosis saveDiagnosis(Diagnosis diagnosis) {
        var entity = DiagnosisJpaEntity.from(diagnosis);
        return repository.save(entity)
                .toDomain();
    }

}
