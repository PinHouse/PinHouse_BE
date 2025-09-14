package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 7) 다자녀 요건 */

@Order(7)
@Component
@RequiredArgsConstructor
public class MultiChildCandidateRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 자녀가 3명 이상이 아니라면 후보 제공
        if (!diagnosis.checkMultiple()) {

            /// 만약 있다면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.MULTICHILD_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "다자녀 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", "자녀가 3명 미만"
                    ));
        }

        return RuleResult.pass(code(),
                "다자녀 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_MULTICHILD_SPECIAL";
    }
}
