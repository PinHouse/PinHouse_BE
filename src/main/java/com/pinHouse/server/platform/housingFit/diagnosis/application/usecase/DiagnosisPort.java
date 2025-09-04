package com.pinHouse.server.platform.housingFit.diagnosis.application.usecase;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;

public interface DiagnosisPort {

    Diagnosis saveDiagnosis(Diagnosis diagnosis);

}
