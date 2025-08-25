package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
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
    public RuleResult evaluate(Diagnosis c) {

        /// 무주택 요건
        if (!c.isHomeless()) {
            return RuleResult.fail(code(), severity(), "무주택자가 아니므로 신청 불가", Map.of("homeless", c.isHomeless()));
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
                "기초 자격 충족, 추천 공급유형 있음",
                Map.of("recommendedSupplyTypes", recommended)
        );
    }


    @Override public String code() {
        return "BASE_ELIGIBILITY";
    }

    @Override public Severity severity() {
        return Severity.HARD_FAIL;
    }
}
