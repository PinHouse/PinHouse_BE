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

/**
 * 미성년자 결혼 특별공급 후보 탐색 규칙
 * - 19세 미만 결혼한 미성년자 대상
 */
@Order(12)
@Component
@RequiredArgsConstructor
public class YouthSpecialCandidateRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 미성년자인지 체크
        int ageMin = policyUseCase.marriedYouthAgeMin();
        boolean ageOk = diagnosis.getAge() < ageMin;

        /// 결혼했는지 여부 확인
        boolean isMarried = diagnosis.isMaritalStatus();

        /// 결혼한 미성년자가 아니라면, 신청불가능
        if (!ageOk || !isMarried) {

            /// 있다면 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);


            // 실패 이유 분류
            String failReason;
            if (!ageOk && !isMarried) {
                failReason = "나이 기준 초과 및 미혼";
            } else if (!ageOk) {
                failReason = "나이 기준 초과";
            } else {
                failReason = "미혼";
            }

            return RuleResult.fail(code(),
                    "미성년자 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReason
                    ));
        }

        /// 미성년자 특별공급 후보
        return RuleResult.pass(code(),
                "미성년자 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override public String code() {
        return "CANDIDATE_YOUTH_SPECIAL";
    }
}
