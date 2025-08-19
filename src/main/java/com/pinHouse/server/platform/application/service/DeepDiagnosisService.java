package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.platform.application.in.diagnosis.DiagnosisUseCase;
import com.pinHouse.server.platform.application.out.diagnosis.DiagnosisPort;
import com.pinHouse.server.platform.domain.diagnosis.rule.RuleChain;
import com.pinHouse.server.platform.domain.diagnosis.entity.RuleContext;
import com.pinHouse.server.platform.domain.diagnosis.rule.RuleExecutionSummary;
import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 마이홈포털 수준의 깊이로 확장한 청약 진단 엔진 예시
 *
 * 설계 키포인트
 * - 단계적 파이프라인(기초자격 → 지역/통장 → 소득/자산 → 특별공급별 규칙 → 일반공급/가점 → 우선순위 산정)
 * - 규칙(Rule) 인터페이스 + 체인(Chain of Responsibility)
 * - 정책(Policy) 외부화: Threshold들을 Provider에서 주입(향후 YAML/DB/어드민에서 변경 가능)
 * - Explainable Result: 모든 통과/실패 사유를 코드 + 메시지 + 세부값으로 축적하여 응답으로 반환
 * - 유지보수: 규칙 클래스를 독립 컴포넌트로 분리하고 @Order로 실행 순서 제어
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DeepDiagnosisService implements DiagnosisUseCase {

    private final DiagnosisPort port;
    private final RuleChain ruleChain;
    private final SupplyDecisionEngine supplyDecisionEngine;

    @Override
    public DiagnosisResult diagnose(DiagnosisRequest request) {
        /// 0) 요청 → 도메인 변환 및 저장
        Diagnosis domain = request.toDomain();
        port.saveDiagnosis(domain);

        /// 1) 컨텍스트 구성 (모든 계산/정책 접근의 허브)
        RuleContext ctx = RuleContext.from(request);

        /// 2) 규칙 체인 실행 (실패/경고/성공 사유 축적)
        RuleExecutionSummary summary = ruleChain.evaluateAll(ctx);
        if (summary.isHardFailed()) {
            return DiagnosisResult.of(false, "불가", summary.primaryFailMessage());
        }

        /// 3) 공급유형 의사결정 (특별공급 후보군 → 적합도/우선순위 산정 → 최종 선택)
        SupplyDecision decision = supplyDecisionEngine.decide(ctx, summary);

        /// 4) 결과 조립 (설명/사유/세부값 포함)
        DiagnosisResult result = DiagnosisResult.of(
                decision.isEligible(),
                decision.getSupplyTypeCode(),
                decision.getDisplayName()
        );
        result.addReasons(summary.toReasons());
        result.addScore(decision.getScore());
        result.addMeta(decision.getMeta());
        return result;
    }
}
