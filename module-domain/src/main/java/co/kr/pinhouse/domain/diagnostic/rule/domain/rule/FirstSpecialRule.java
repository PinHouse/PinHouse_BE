package co.kr.pinhouse.domain.diagnostic.rule.domain.rule;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.Diagnosis;
import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus;
import co.kr.pinhouse.domain.diagnostic.rule.application.dto.RuleResult;
import co.kr.pinhouse.domain.diagnostic.rule.application.usecase.PolicyUseCase;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.EvaluationContext;
import co.kr.pinhouse.domain.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;

/**
 * 생애 최초 특별 공급
 */

@Order(5)
@Component
@RequiredArgsConstructor
public class FirstSpecialRule implements Rule {

	/// 임대주택 유형 검증기 도입
	private final PolicyUseCase policyUseCase;

	@Override
	public RuleResult evaluate(EvaluationContext ctx) {

		Diagnosis diagnosis = ctx.getDiagnosis();

		/// 가능한 리스트 추출하기
		var candidates = new ArrayList<>(ctx.getCurrentCandidates());

		/// 무주택 세대주 여부
		boolean noOwnHome = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE);
		boolean isHouseholdHead = diagnosis.isHouseholdHead();

		/// 혼인 중이거나 자녀가 있는 경우
		boolean isMarried = diagnosis.isMaritalStatus();
		boolean hasChildren = (diagnosis.getUnbornChildrenCount()
			+ diagnosis.getUnder6ChildrenCount()
			+ diagnosis.getOver7MinorChildrenCount()) > 0;

		/// 생애최초 요건: 무주택 세대주 + (결혼했거나 자녀가 있음)
		boolean qualifies = noOwnHome && isHouseholdHead && (isMarried || hasChildren);

		if (!qualifies) {

			/// 만약 있다면 삭제
			candidates.removeIf(c ->
				c.supplyType() == SupplyType.FIRST_SPECIAL);

			/// 결과 저장하기
			ctx.setCurrentCandidates(candidates);

			// 실패 이유 분류
			String failReason;
			if (!noOwnHome) {
				failReason = "무주택 세대 요건 미충족";
			} else if (!isHouseholdHead) {
				failReason = "세대주가 아님";
			} else {
				failReason = "혼인 또는 자녀 요건 미충족";
			}

			return RuleResult.fail(code(),
				"생애최초 특별공급 해당 없음",
				Map.of(
					"candidate", candidates,
					"failReason", failReason
				));
		}

		return RuleResult.pass(code(),
			"생애최초 특별공급 후보",
			Map.of("candidate", candidates));

	}

	@Override
	public String code() {
		return "FIRST_SPECIAL";
	}
}
