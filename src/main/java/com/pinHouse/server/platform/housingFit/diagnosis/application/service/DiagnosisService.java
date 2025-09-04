package com.pinHouse.server.platform.housingFit.diagnosis.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.request.DiagnosisRequest;
import com.pinHouse.server.platform.housingFit.diagnosis.application.dto.response.DiagnosisResponse;
import com.pinHouse.server.platform.housingFit.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.repository.DiagnosisJpaRepository;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.service.RuleChain;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.application.service.RuleExecutionSummary;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


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
public class DiagnosisService implements DiagnosisUseCase {

    /// 의존성
    private final DiagnosisJpaRepository repository;

    /// 룰 체인
    private final RuleChain ruleChain;

    /// 유저 의존성
    private final UserUseCase userService;

    /// 진단하기
    @Override
    public DiagnosisResponse diagnose(UUID userId, DiagnosisRequest request) {

        /// 유저 예외 처리
        User user = userService.loadUserById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));

        /// 진단 도메인 생성
        Diagnosis diagnosis = Diagnosis.of(user, request);

        Diagnosis ctx = repository.save(diagnosis);

        // 규칙 체인 실행
        RuleExecutionSummary summary = ruleChain.evaluateAll(ctx);

        // 강제 실패 체크 없이 summary 결과 그대로 반환
        List<RuleResult> results = summary.getResults();// List<RuleResult>
        return null;
    }



}
