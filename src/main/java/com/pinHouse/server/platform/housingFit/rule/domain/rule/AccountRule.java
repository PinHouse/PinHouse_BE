package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.SubscriptionAccount;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.SubscriptionCount;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.SubscriptionPeriod;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.RentalType;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyRentalCandidate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pinHouse.server.platform.housingFit.rule.domain.entity.RentalType.*;

/** 3) 청약통장 요건(가입기간/예치금/상품유형) */
@Order(2)
@Component
@RequiredArgsConstructor
public class AccountRule implements Rule {

    /// 청약통장이 없으면 불가능한 목록들
    private static final List<RentalType> NO_ACCOUNT_ALLOWED = List.of(
            NATIONAL_RENTAL,
            PUBLIC_INTEGRATED,
            LONG_TERM_JEONSE,
            PUBLIC_RENTAL
    );


    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        /// 진단 도메인 정보 가져오기
        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 규칙 돌기
        if (!diagnosis.isHasAccount()) {

            /// 청약통장 없는 경우, NO_ACCOUNT_ALLOWED에 해당하는 RentalType 제거
            candidates.removeIf(c -> NO_ACCOUNT_ALLOWED.contains(c.rentalType()));

            return RuleResult.pass(code(),
                    "청약통장 미보유로 인한 유형 제거 완료",
                    Map.of("candidate", candidates));
        }

        //TODO! 추후 가입기간에 따른 가산점으로 변경
        /// 가입 기간 체크
        SubscriptionPeriod accountYears = diagnosis.getAccountYears();
        double years = accountYears.getYears(); // 실제 연도 값

        /// 공공임대 내부에서, 가입기간에 따른 불가능 제거
        removePUBLIC_RENTAL(candidates, years, diagnosis);

        String message = String.format("청약통장 가입기간 %.1f년, 가입기간이 길수록 우선순위가 높습니다.", years);

        ///후보군은 그대로 유지
        ctx.setCurrentCandidates(candidates);

        // 조건 통과
        return RuleResult.pass(code(),
                message,
                Map.of("accountYears", diagnosis.getAccountYears()));
    }

    private static void removePUBLIC_RENTAL(ArrayList<SupplyRentalCandidate> candidates, double years, Diagnosis diagnosis) {

        for (SupplyRentalCandidate c : new ArrayList<>(candidates)) { // 반복 중 수정 방지
            if (c.rentalType() != PUBLIC_RENTAL) continue;

            switch (c.supplyType()) {
                case FIRST_SPECIAL -> {
                    // 생애최초: 납입금액 600만원 미만이면 삭제
                    if (diagnosis.getAccount() == SubscriptionAccount.UNDER_600) {
                        candidates.remove(c);
                    }
                }

                case YOUTH_SPECIAL, MINOR_SPECIAL, MULTICHILD_SPECIAL, NEWCOUPLE_SPECIAL -> {
                    // 가입 6개월 이상 + 6회 이상 납부 조건 체크
                    boolean failPeriod = years <= 0.5;
                    boolean failDepositCount = diagnosis.getAccountDeposit() == SubscriptionCount.FROM_0_TO_5;

                    if (failPeriod || failDepositCount) {
                        candidates.remove(c);
                    }
                }

                default -> {
                    // 그 외는 삭제하지 않음
                }
            }
        }
    }

    @Override public String code() {
        return "ACCOUNT_REQUIREMENT";
    }


}
