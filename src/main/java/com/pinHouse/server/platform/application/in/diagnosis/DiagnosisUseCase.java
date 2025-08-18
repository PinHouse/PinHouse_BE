package com.pinHouse.server.platform.application.in.diagnosis;

import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisResult;

public interface DiagnosisUseCase {

    DiagnosisResult diagnose(DiagnosisRequest request);

}
