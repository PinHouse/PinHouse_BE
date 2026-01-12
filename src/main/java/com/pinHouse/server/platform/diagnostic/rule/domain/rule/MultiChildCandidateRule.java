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

/** 7) 다자녀 요건 */

@Order(7)
@Component
@RequiredArgsConstructor
public class MultiChildCandidateRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 가능한 리스트 추출하기
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 미성년 자녀 수 계산 (태아 + 6세 이하 + 7세 이상 미성년)
        int minorChildCount = diagnosis.getUnbornChildrenCount() +
                              diagnosis.getUnder6ChildrenCount() +
                              diagnosis.getOver7MinorChildrenCount();

        /// 다자녀 특별공급 자격 요건:
        /// - 통합공공임대/국민임대/장기전세: 미성년 자녀 2명 이상
        /// - 공공임대: 미성년 자녀 3명 이상

        /// Iterator로 후보 순회
        var iter = candidates.iterator();
        while (iter.hasNext()) {
            var candidate = iter.next();

            if (candidate.supplyType() == SupplyType.MULTICHILD_SPECIAL) {
                var noticeType = candidate.noticeType();

                /// 공공임대는 3명 이상 필요
                if (noticeType == com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType.PUBLIC_RENTAL) {
                    if (minorChildCount < 3) {
                        iter.remove();
                    }
                }
                /// 그 외 (통합공공임대, 국민임대, 장기전세)는 2명 이상 필요
                else {
                    if (minorChildCount < 2) {
                        iter.remove();
                    }
                }
            }
        }

        /// 결과 저장하기
        ctx.setCurrentCandidates(candidates);

        /// 다자녀 특별공급 후보가 남아있는지 확인
        boolean hasMultiChildCandidate = candidates.stream()
                .anyMatch(c -> c.supplyType() == SupplyType.MULTICHILD_SPECIAL);

        if (!hasMultiChildCandidate) {
            return RuleResult.fail(code(),
                    "다자녀 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", "미성년 자녀 수 요건 미충족 (현재: " + minorChildCount + "명)"
                    ));
        }

        return RuleResult.pass(code(),
                "다자녀 특별공급 후보",
                Map.of("candidate", candidates, "minorChildCount", minorChildCount));
    }

    @Override public String code() {
        return "CANDIDATE_MULTICHILD_SPECIAL";
    }
}
