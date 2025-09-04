package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;

interface Rule {

    RuleResult evaluate(EvaluationContext ctx);

    String code();

    Severity severity();
}

