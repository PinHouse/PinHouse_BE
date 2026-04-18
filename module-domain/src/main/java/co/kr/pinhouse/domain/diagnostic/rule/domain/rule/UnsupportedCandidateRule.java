package co.kr.pinhouse.domain.diagnostic.rule.domain.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.diagnostic.rule.application.dto.RuleResult;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.EvaluationContext;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.SupplyRentalCandidate;

/**
 * 현재 DTO만으로 최신 공식 기준을 정확히 재현할 수 없는 후보를 보수적으로 제거한다.
 */
@Order(16)
@Component
public class UnsupportedCandidateRule implements Rule {

	@Override
	public RuleResult evaluate(EvaluationContext ctx) {
		var candidates = new ArrayList<>(ctx.getCurrentCandidates());
		List<SupplyRentalCandidate> removed = new ArrayList<>();

		candidates.removeIf(candidate -> {
			boolean unsupported = !candidate.policyKey().exactSupported();

			if (unsupported) {
				removed.add(candidate);
			}
			return unsupported;
		});

		ctx.setCurrentCandidates(candidates);

		if (removed.isEmpty()) {
			return RuleResult.pass(code(),
				"DTO로 정확 판정 가능한 후보만 유지",
				Map.of("candidate", candidates));
		}

		return RuleResult.pass(code(),
			"현재 DTO로 정확 판정이 어려운 후보 제거",
			Map.of(
				"candidate", candidates,
				"removed", removed.stream()
					.map(candidate -> String.format("%s : %s (%s)",
						candidate.noticeType().getValue(),
						candidate.supplyType().getValue(),
						candidate.policyKey().unsupportedReason()))
					.toList()
			));
	}

	@Override
	public String code() {
		return "UNSUPPORTED_CANDIDATE_FILTER";
	}
}
