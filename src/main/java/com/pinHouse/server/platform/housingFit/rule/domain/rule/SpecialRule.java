package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.SpecialCategory;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Order(10)
@Component
@RequiredArgsConstructor
public class SpecialRule implements Rule{

    /// 임대주택 로직
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 가지고있는 특수계층 여부
        List<SpecialCategory> specialCategory = diagnosis.getHasSpecialCategory();

        /// 모든 특수계층 남겨두기
        List<SupplyType> specialTypes = SupplyType.getSpecialTypes();

        /// 특수계층을 하나라도 가지고 있다면,
        if (!specialCategory.isEmpty()) {

            /// 사용자가 가진 특수계층을 SupplyType으로 매핑
            List<SupplyType> ownedTypes = specialCategory.stream()
                    .map(SpecialCategory::toSupplyType)
                    .toList();

            /// 존재하는 계층은 냅두고, 없는 것은 삭제하기
            candidates.removeIf(c ->
                    specialTypes.contains(c.supplyType())
                            && !ownedTypes.contains(c.supplyType()));

            ctx.setCurrentCandidates(candidates);

            return RuleResult.pass(code(),
                    "특수계층 공급 후보",
                    Map.of("candidate", candidates));

        }

        /// 특별계층이 하나도 없다면 삭제
        else {

            /// 모든 특별계층이 있는 것 삭제
            specialTypes.forEach(supplyType -> {
                candidates.removeIf(c ->
                        c.supplyType() == supplyType);

            });
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "특수계층 공급 후보 없음",
                    Map.of("candidate", candidates));
        }
    }

    @Override
    public String code() {
        return "";
    }
}
