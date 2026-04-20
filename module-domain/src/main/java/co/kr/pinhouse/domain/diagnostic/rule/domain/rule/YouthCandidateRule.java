package co.kr.pinhouse.domain.diagnostic.rule.domain.rule;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.Diagnosis;
import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus;
import co.kr.pinhouse.domain.diagnostic.rule.application.dto.RuleResult;
import co.kr.pinhouse.domain.diagnostic.rule.application.usecase.PolicyUseCase;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.CandidatePolicyKey;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.EvaluationContext;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;

/**
 * 청년 특별공급 후보 탐색 규칙
 * - 19~39세 무주택 청년
 * - 세대주 또는 예비 세대주
 */
@Order(13)
@Component
@RequiredArgsConstructor
public class YouthCandidateRule implements Rule {

	private final PolicyUseCase policyUseCase;

	@Override
	public RuleResult evaluate(EvaluationContext ctx) {

		Diagnosis diagnosis = ctx.getDiagnosis();

		/// 가능한 리스트 추출하기
		var candidates = new ArrayList<>(ctx.getCurrentCandidates());

		int age = diagnosis.getAge();
		boolean unmarried = !diagnosis.isMaritalStatus();
		boolean isNoHouse = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
			|| diagnosis.getHousingStatus().equals(HousingOwnershipStatus.HOUSEHOLD_MEMBER_OWNS_HOUSE);

		candidates.removeIf(candidate -> {
			if (candidate.supplyType() != SupplyType.YOUTH_SPECIAL) {
				return false;
			}

			int minAge = candidate.policyKey() == CandidatePolicyKey.INTEGRATED_YOUTH_18_TO_39
				? 18
				: policyUseCase.youthAgeMin();
			boolean ageOk = age >= minAge && age <= 39;
			return !(ageOk && unmarried && isNoHouse);
		});

		ctx.setCurrentCandidates(candidates);

		boolean hasYouthCandidate = candidates.stream()
			.anyMatch(candidate -> candidate.supplyType() == SupplyType.YOUTH_SPECIAL);

		if (!hasYouthCandidate) {
			String failReason;
			if (diagnosis.isMaritalStatus()) {
				failReason = "청년 계층 혼인 요건 미충족";
			} else if (!isNoHouse) {
				failReason = "무주택 요건 미충족";
			} else if (age < 18) {
				failReason = "나이 기준 미충족 (통합공공임대 청년 18세 이상)";
			} else {
				failReason = "나이 기준 미충족";
			}

			return RuleResult.fail(code(),
				"청년 특별공급 해당 없음",
				Map.of(
					"candidate", candidates,
					"failReason", failReason
				));
		}

		return RuleResult.pass(code(),
			"청년 특별공급 후보",
			Map.of("candidate", candidates));
	}

	@Override
	public String code() {
		return "CANDIDATE_YOUTH_SPECIAL";
	}
}
