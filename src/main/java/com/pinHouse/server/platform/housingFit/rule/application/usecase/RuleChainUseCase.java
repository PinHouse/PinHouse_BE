package com.pinHouse.server.platform.housingFit.rule.application.usecase;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;

public interface RuleChainUseCase {

    /// 도메인 기반으로 룰 진행하기
    EvaluationContext evaluateAll(Diagnosis diagnosis);

}
