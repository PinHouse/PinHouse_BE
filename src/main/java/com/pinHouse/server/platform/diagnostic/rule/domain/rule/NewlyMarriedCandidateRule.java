package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 6) 신혼 부부 요건 */

@Order(8)
@Component
@RequiredArgsConstructor
public class NewlyMarriedCandidateRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 결혼했는지 체크
        boolean isMarried = diagnosis.isMaritalStatus();

        /// 7년 이하의 결혼 체크
        int marriedMaxYears = policyUseCase.newlyMarriedMaxYears();
        boolean withinYears = diagnosis.getMarriageYears() != null && diagnosis.getMarriageYears() <= marriedMaxYears;

        /// 자녀가 6세 이하라면 true
        boolean withYouthAge = diagnosis.getUnbornChildrenCount() > 0 || diagnosis.getUnder6ChildrenCount() > 0;

        /// 신혼부부 특별공급 요건이 안된다면,
        /// - 결혼했고 7년 이하, 또는
        /// - 결혼 7년 초과지만 자녀가 6세 이하인 경우

        if (!(isMarried && (withinYears || withYouthAge))) {

            /// 있다면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.NEWCOUPLE_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            /// 실패 이유를 담아줌
            String failReason;
            if (!isMarried) {
                failReason = "결혼하지 않음";
            } else if (!withinYears && !withYouthAge) {
                failReason = "결혼 7년 초과 및 자녀 요건 미충족";
            } else if (!withinYears) {
                failReason = "결혼 7년 초과";
            } else {
                failReason = "자녀 요건 미충족";
            }

            return RuleResult.fail(code(),
                    "신혼부부 조건 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReason
                    ));
        }


        return RuleResult.pass(code(),
                "신혼부부 해당 조건 충족",
                Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_NEWCOUPLE_SPECIAL";
    }
}
