package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/** 1) 기초 자격: 나이 + 세대주 + 무주택  */
@Component
@Order(1)
public class BaseEligibilityRule implements Rule {

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis c = ctx.getDiagnosis();

        /// 주택 소유 여부
        boolean hasHousehold = c.isHasHousehold();
        if (hasHousehold) {
            return RuleResult.fail(
                    code(),
                    Severity.HARD_FAIL,
                    "주택 소유",
                    Map.of("household", hasHousehold)
            );
        }

        /// 나이에 따른 추천 유형 매핑
        int age = c.getAge();

        /// 나이에 따른 가능한 것 전부 추출
        List<SupplyType> recommended = new ArrayList<>();

        /// 미성년자 일 때는, 특수한 경우만 가능
        if (age < 19) {
            recommended.add(SupplyType.SPECIAL);
        }

        /// 성인만 신청 가능
        if (age >= 19) {
            recommended.add(SupplyType.GENERAL);
        }

        /// 성인이면서 만 39세 이하일 경우, 청년 신청 가능
        if (age >= 19 && age <= 39) {
            recommended.add(SupplyType.YOUTH_SPECIAL);
        }

        /// 만 64세까지 신혼부부 혜택은 다 가능
        if (age <= 64) {
            recommended.add(SupplyType.NEWCOUPLE_SPECIAL);
        }

        if (age >= 65) {
            recommended.add(SupplyType.ELDER_SPECIAL);
        }

        return RuleResult.pass(
                code(),
                Severity.INFO,
                "나이별 추천 타입 후보",
                Map.of("candidate", recommended)
        );
    }


    @Override public String code() {
        return "BASE_ELIGIBILITY";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }
}
