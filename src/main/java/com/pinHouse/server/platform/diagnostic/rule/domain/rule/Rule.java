package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.response.RuleResult;

public interface Rule {

    RuleResult evaluate(EvaluationContext ctx);

    String code();
}

