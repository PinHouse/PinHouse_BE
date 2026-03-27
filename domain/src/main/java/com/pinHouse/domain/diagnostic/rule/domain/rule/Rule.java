package com.pinHouse.domain.diagnostic.rule.domain.rule;

import com.pinHouse.domain.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.domain.diagnostic.rule.application.dto.RuleResult;

public interface Rule {

    RuleResult evaluate(EvaluationContext ctx);

    String code();
}

