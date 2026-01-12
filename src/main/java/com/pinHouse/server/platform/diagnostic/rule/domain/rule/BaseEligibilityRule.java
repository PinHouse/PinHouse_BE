package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyRentalCandidate;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;

/** 1) 기초 자격: 나이 + 세대주 + 무주택  */
@Order(1)
@Component
@RequiredArgsConstructor
public class BaseEligibilityRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 1차, 나이에 따른 추천 유형 매핑
        int age = diagnosis.getAge();

        /// 가능한 리스트 추출하기
        List<SupplyRentalCandidate> candidates = ctx.getCurrentCandidates();

        /// 나이별 후보군 필터링
        if (age < 19) {
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.NEWCOUPLE_SPECIAL ||
                            c.supplyType() == SupplyType.ELDER_SPECIAL ||
                            c.supplyType() == SupplyType.GENERAL
            );
        } else if (age <= 39) {
            candidates.removeIf(c -> c.supplyType() == SupplyType.ELDER_SPECIAL);
        } else if (age <= 64) {
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.YOUTH_SPECIAL ||
                            c.supplyType() == SupplyType.ELDER_SPECIAL
            );
        } else if (age >= 65) {
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.YOUTH_SPECIAL ||
                            c.supplyType() == SupplyType.NEWCOUPLE_SPECIAL
            );
        }

        /// 2차, 주택 소유 여부
        /// - 통합공공임대: 무주택세대구성원 또는 무주택자
        /// - 영구임대: 생계급여 또는 의료급여 수급자 등
        /// - 국민임대/장기전세/공공임대: 무주택세대구성원
        /// - 행복주택: 무주택세대구성원 또는 무주택자

        var housingStatus = diagnosis.getHousingStatus();
        boolean ownsHouse = housingStatus.equals(
                com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus.OWN_HOUSE);

        /// 본인이 주택을 소유한 경우 모든 임대주택 신청 불가
        if (ownsHouse) {
            return RuleResult.fail(
                    code(),
                    "주택 소유자는 임대주택 지원이 불가능",
                    Map.of("housingStatus", housingStatus)
            );
        }

        /// 세대구성원 중 주택 소유자가 있는 경우
        /// - 통합공공임대, 행복주택: 무주택자로도 신청 가능하므로 허용
        /// - 국민임대, 장기전세, 공공임대: 무주택세대구성원만 가능하므로 일부 제한
        boolean householdMemberOwnsHouse = housingStatus.equals(
                com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus.HOUSEHOLD_MEMBER_OWNS_HOUSE);

        if (householdMemberOwnsHouse) {
            /// 무주택세대구성원이 필수인 경우 해당 후보 제거
            candidates.removeIf(c ->
                c.noticeType() == com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType.NATIONAL_RENTAL ||
                c.noticeType() == com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType.LONG_TERM_JEONSE ||
                c.noticeType() == com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType.PUBLIC_RENTAL
            );
        }

        /// 결과 저장하기
        ctx.setCurrentCandidates(candidates);

        return RuleResult.pass(
                code(),
                "나이별 추천 타입 후보 완료",
                Map.of(
                        "age", age,
                        "candidates", candidates
                )
        );


    }

    /**
     * 코드 저장
     */
    @Override public String code() {
        return "BASE_ELIGIBILITY";
    }
}
