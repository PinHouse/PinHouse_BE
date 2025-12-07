package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.HousingOwnershipStatus;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.SubscriptionPeriod;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 임대주택 일반공급 후보 탐색 규칙
 *
 * <청약통장 필요 여부>
 * - 공공임대(PUBLIC_RENTAL), 통합공공임대(PUBLIC_INTEGRATED): 청약통장 6개월 이상 필요
 *   (분양전환형 임대주택으로 추후 소유권 전환 가능)
 *
 * - 국민임대, 영구임대, 장기전세, 행복주택: 청약통장 불필요
 *   (순수 임대주택으로 소유권 전환 불가)
 *
 * <공통 요건>
 * - 무주택 세대구성원이어야 함
 * - 소득/자산 기준은 별도 규칙(IncomeAssetRule 등)에서 검증
 */
@Order(14)
@Component
@RequiredArgsConstructor
public class GeneralSupplyRule implements Rule {

    private final PolicyUseCase policyUseCase;

    /// 청약통장이 필요한 임대주택 유형
    private static final Set<NoticeType> SUBSCRIPTION_REQUIRED_TYPES = Set.of(
            NoticeType.PUBLIC_RENTAL,        // 공공임대 (분양전환 가능)
            NoticeType.PUBLIC_INTEGRATED     // 통합공공임대 (분양전환 가능)
    );

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 무주택 세대 여부 (가구원 중 주택 소유자가 없는 경우)
        boolean isNoHouseHold = diagnosis.getHousingStatus().equals(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE);

        /// 청약통장 정보
        boolean hasAccount = diagnosis.isHasAccount();
        boolean hasAccountOver6Months = hasAccount
                && diagnosis.getAccountYears() != null
                && diagnosis.getAccountYears() != SubscriptionPeriod.LESS_THAN_6_MONTHS;

        /// 일반공급 후보 필터링
        candidates.removeIf(c -> {
            if (c.supplyType() != SupplyType.GENERAL) {
                return false; // GENERAL이 아니면 제거하지 않음
            }

            /// 무주택 세대 요건 미충족
            if (!isNoHouseHold) {
                return true; // 제거
            }

            /// 청약통장이 필요한 임대주택 유형인 경우
            if (SUBSCRIPTION_REQUIRED_TYPES.contains(c.noticeType())) {
                /// 청약통장 6개월 이상 미보유 시 제거
                return !hasAccountOver6Months;
            }

            /// 청약통장이 불필요한 임대주택 유형 (국민임대, 영구임대 등)
            return false; // 제거하지 않음
        });

        /// 결과 저장하기
        ctx.setCurrentCandidates(candidates);

        /// 일반공급 후보가 있는지 확인
        boolean hasGeneralCandidate = candidates.stream()
                .anyMatch(c -> c.supplyType() == SupplyType.GENERAL);

        if (!hasGeneralCandidate) {
            String failReason = "";
            if (!isNoHouseHold) {
                failReason = "무주택 세대 요건 미충족";
            } else if (!hasAccountOver6Months) {
                failReason = "청약통장 6개월 이상 미보유 (공공임대/통합공공임대 요건 미충족)";
            }

            return RuleResult.fail(code(),
                    "일반공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReason
                    ));
        }

        /// 일반공급 후보
        return RuleResult.pass(code(),
                "일반공급 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "CANDIDATE_GENERAL";
    }
}
