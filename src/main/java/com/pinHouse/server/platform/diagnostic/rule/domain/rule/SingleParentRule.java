package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.SpecialCategory;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.SpecialCategory.PROTECTED_SINGLE_PARENT;
import static com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.SpecialCategory.SINGLE_PARENT;

/**
 * 한부모 특별 공급 조사
 */

@Order(9)
@Component
@RequiredArgsConstructor
public class SingleParentRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 한부모 가정 여부
        List<SpecialCategory> specialCategories = diagnosis.getHasSpecialCategory();

        /// 한부모인지
        boolean hasSingleParent = specialCategories.stream()
                .anyMatch(c -> c == SINGLE_PARENT || c == PROTECTED_SINGLE_PARENT);

        if (!hasSingleParent) {

            /// 한부모 계층이 아니라면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.SINGLE_PARENT_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "한부모 특별공급 해당 없음",
                    Map.of("candidate", candidates));
        }

        return RuleResult.pass(code(),
                "한부모 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "SINGLE_PARENT_SPECIAL";
    }
}
