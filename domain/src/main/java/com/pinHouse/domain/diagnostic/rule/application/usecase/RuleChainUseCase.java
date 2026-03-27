package com.pinHouse.domain.diagnostic.rule.application.usecase;

import com.pinHouse.domain.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.domain.diagnostic.rule.domain.entity.EvaluationContext;

public interface RuleChainUseCase {

    /// 도메인 기반으로 룰 진행하기
    EvaluationContext evaluateAll(Diagnosis diagnosis);

}
