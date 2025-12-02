package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/**
 * 일반공급 후보 탐색 규칙
 * - 무주택 세대주
 * - 청약통장 보유
 */
@Order(14)
@Component
@RequiredArgsConstructor
public class GeneralSupplyRule implements Rule {

    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 세대주 여부
        boolean isHouseholdHead = diagnosis.isHouseholdHead();

        /// 무주택 세대 여부
        boolean isNoHouse = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE);

        /// 청약통장 보유 여부
        boolean hasAccount = diagnosis.isHasAccount();

        /// 청약통장 가입기간 체크 (일반공급은 최소 6개월 이상 가입 필요)
        boolean hasMinAccountPeriod = hasAccount &&
            diagnosis.getAccountYears() != null &&
            diagnosis.getAccountYears().getYears() >= 0.5;

        /// 일반공급 요건: 무주택 세대주 + 청약통장 보유 + 가입기간 6개월 이상
        boolean qualifies = isHouseholdHead && isNoHouse && hasAccount && hasMinAccountPeriod;

        if (!qualifies) {

            /// 일반공급 제거
            candidates.removeIf(c -> c.supplyType() == SupplyType.GENERAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            // 실패 이유 분류
            String failReason;
            if (!isHouseholdHead) {
                failReason = "세대주가 아님";
            } else if (!isNoHouse) {
                failReason = "무주택 세대 요건 미충족";
            } else if (!hasAccount) {
                failReason = "청약통장 미보유";
            } else {
                failReason = "청약통장 가입기간 부족 (최소 6개월 필요)";
            }

            return RuleResult.fail(code(),
                    "일반공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReason
                    ));
        }

        /// 일반공급 후보
        return RuleResult.pass(code(),
                "일반공급 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "CANDIDATE_GENERAL";
    }
}
