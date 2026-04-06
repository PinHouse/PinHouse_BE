package com.pinHouse.domain.diagnostic.rule.domain.rule;

import com.pinHouse.domain.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.domain.diagnostic.rule.domain.entity.EvaluationContext;

public interface Rule {

	RuleResult evaluate(EvaluationContext ctx);

	String code();
}
