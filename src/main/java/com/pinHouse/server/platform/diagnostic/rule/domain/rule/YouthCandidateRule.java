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
 * 청년 특별공급 후보 탐색 규칙
 * - 19~39세 무주택 청년
 * - 세대주 또는 예비 세대주
 */
@Order(13)
@Component
@RequiredArgsConstructor
public class YouthCandidateRule implements Rule {

    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 나이 체크 (19~39세)
        int age = diagnosis.getAge();
        int youthAgeMin = policyUseCase.youthAgeMin();
        boolean ageOk = age >= youthAgeMin && age <= 39;

        /// 무주택 세대주 또는 예비 세대주 여부
        boolean isHouseholdHead = diagnosis.isHouseholdHead();
        boolean isNoHouse = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE) ||
                diagnosis.getHousingStatus().equals(HousingOwnershipStatus.HOUSEHOLD_MEMBER_OWNS_HOUSE);

        /// 1인 가구 또는 세대주인 경우 청년 특별공급 가능
        boolean qualifies = ageOk && (isHouseholdHead || diagnosis.isSingle()) && isNoHouse;

        if (!qualifies) {

            /// 청년 특별공급 제거
            candidates.removeIf(c -> c.supplyType() == SupplyType.YOUTH_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            // 실패 이유 분류
            String failReason;
            if (!ageOk) {
                failReason = "나이 기준 미충족 (19~39세)";
            } else if (!isNoHouse) {
                failReason = "무주택 요건 미충족";
            } else {
                failReason = "세대주 또는 1인 가구 요건 미충족";
            }

            return RuleResult.fail(code(),
                    "청년 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReason
                    ));
        }

        /// 청년 특별공급 후보
        return RuleResult.pass(code(),
                "청년 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "CANDIDATE_YOUTH_SPECIAL";
    }
}
