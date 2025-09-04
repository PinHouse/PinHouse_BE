package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.Rule;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.Severity;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 6) 신혼 부부 요건 */
@Component
@Order(6)
public class NewlyMarriedCandidateRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();
        /// 이전 내용들 가져오기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        if (candidates.contains(SupplyType.NEWCOUPLE_SPECIAL)) {
            /// 결혼했는지 체크
            boolean isMarried = c.isMaritalStatus();

            /// 7년 이하의 결혼 체크
            boolean withinYears = c.getMarriageYears() != null && c.getMarriageYears() <= c.getPolicy().newlyMarriedMaxYears();

            /// 자녀가 6세 이하라면 true
            boolean withYouthAge = c.getUnbornChildrenCount() > 0 || c.getUnder6ChildrenCount() > 0;

            /// 신혼부부 특별공급 요건이 안된다,
            /// - 결혼했고 7년 이하, 또는
            /// - 결혼 7년 초과지만 자녀가 6세 이하인 경우
            if (!(isMarried && (withinYears || withYouthAge))) {
                candidates.remove(SupplyType.NEWCOUPLE_SPECIAL);
            }
        }

        return RuleResult.pass(code(), severity(), "신혼부부 해당 조건 충족", Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_NEWCOUPLE_SPECIAL";
    }

    @Override public Severity severity() {
        return Severity.INFO;
    }

}
