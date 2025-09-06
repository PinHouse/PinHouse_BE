package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;

public interface Rule {

    RuleResult evaluate(EvaluationContext ctx);

    String code();
}

