package com.pinHouse.server.platform.diagnostic.rule.domain.entity;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * 진단을 위한 평가 배경
 */
@Getter
@AllArgsConstructor
@Builder
public class EvaluationContext {

    /// 진단 엔티티
    private final Diagnosis diagnosis;

    /// 저장할 결과 목록
    private final List<RuleResult> ruleResults;

    /// 진단에서 가능한 후보군 목록
    private List<SupplyRentalCandidate> currentCandidates;


    /// 정적 팩토리 메서드
    public static EvaluationContext of(Diagnosis diagnosis) {
        return EvaluationContext.builder()
                .diagnosis(diagnosis)
                .ruleResults(new ArrayList<>())
                /// 모든 공급유형을 추가해두고 삭제하는 방식으로 구현
                .currentCandidates(SupplyRentalCandidate.basic())
                .build();
    }

//    /**
//     * 하나의 Rule을 진행할 때마다, RuleResult 값 더하기
//     * @param result    룰 결과
//     */
//    public void addResult(RuleResult result) {
//
//        /// 최종결과에 하나의 Rule에 대한 결과 추가
//        ruleResults.add(result);
//
//        /// Rule을 통과하지 못했다면, 공급유형에서 제거하도록 설정
//        if (!result.pass()) {
//            SupplyType candidate = (SupplyType) result.details().get("candidate");
//            currentCandidates.remove(candidate);
//        }
//    }


    /**
     * 함수
     * @param candidates
     */
    public void setCurrentCandidates(List<SupplyRentalCandidate> candidates) {
        this.currentCandidates = candidates;
    }
}
