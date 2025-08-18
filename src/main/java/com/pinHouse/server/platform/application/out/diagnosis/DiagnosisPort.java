package com.pinHouse.server.platform.application.out.diagnosis;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;

public interface DiagnosisPort {

    Diagnosis saveDiagnosis(Diagnosis diagnosis);

}
