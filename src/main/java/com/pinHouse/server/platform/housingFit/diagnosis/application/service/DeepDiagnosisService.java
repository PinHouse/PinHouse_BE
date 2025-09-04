package com.pinHouse.server.platform.housingFit.diagnosis.application.service;

import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisQuestion;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisType;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.DiagnosisJpaRepository;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.service.SupplyDecisionEngine;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.RuleChain;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.RuleExecutionSummary;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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

    private final DiagnosisJpaRepository repository;

    private final RuleChain ruleChain;
    private final SupplyDecisionEngine supplyDecisionEngine;


    /// 질문 받기
    @Override
    public DiagnosisQuestion getDiagnose(DiagnosisType type) {
        return null;
    }

    /// 진단하기
    @Override
    public List<RuleResult> diagnose(DiagnosisRequest request) {
        // 0) 요청 → 도메인 변환 및 저장
        Diagnosis domain = request.toDomain();

        Diagnosis ctx = repository.save(domain);

        // 1) 규칙 체인 실행
        RuleExecutionSummary summary = ruleChain.evaluateAll(ctx);

        // 2) 강제 실패 체크 없이 summary 결과 그대로 반환
        return summary.getResults(); // List<RuleResult>
    }



}
