package co.kr.pinhouse.domain.diagnostic.rule.application.usecase;

import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.Diagnosis;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.EvaluationContext;

public interface RuleChainUseCase {

	/// 도메인 기반으로 룰 진행하기
	EvaluationContext evaluateAll(Diagnosis diagnosis);

}
