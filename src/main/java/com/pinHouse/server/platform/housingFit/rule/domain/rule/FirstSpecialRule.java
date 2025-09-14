package com.pinHouse.server.platform.housingFit.rule.domain.rule;


import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.HousingOwnershipStatus;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

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

        /// 무주택자 여부
        boolean noOwnHome = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE);

        if (!noOwnHome) {

            /// 만약 있다면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.FIRST_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "무주택자 해당 없음",
                    Map.of("candidate", candidates));
        }

        return RuleResult.fail(code(),
                "생애최초 특별공급 후보",
                Map.of("candidate", candidates));

    }


    @Override
    public String code() {
        return "FIRST_SPECIAL";
    }
}
