package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.SpecialCategory.SUPPORTING_ELDERLY;


/** 8) 노부모 부양 지원 */
@Order(4)
@Component
@RequiredArgsConstructor
public class ElderSupportCandidateRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {
        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 부양한 노부모가 없다면 제거
        if (!diagnosis.getHasSpecialCategory().contains(SUPPORTING_ELDERLY)) {

            /// 만약 있다면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.YOUTH_SPECIAL ||
                            c.supplyType() == SupplyType.ELDER_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            /// 조건이 없으므로 넘어간다.
            return RuleResult.fail(code(),
                    "노부모 부양 특별공급 해당 없음",
                    Map.of("candidate", candidates));
        }

        /// 조건 충족으로 넘어간다.
        return RuleResult.pass(code(),
                "노부모 부양 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_ELDER_SUPPORT_SPECIAL";
    }

}
