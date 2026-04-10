package co.kr.pinhouse.domain.diagnostic.rule.domain.rule;

import co.kr.pinhouse.domain.diagnostic.rule.application.dto.RuleResult;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.EvaluationContext;

public interface Rule {

	RuleResult evaluate(EvaluationContext ctx);

	String code();
}
