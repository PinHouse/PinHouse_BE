package com.pinHouse.server.platform.diagnostic.diagnosis.application.service;

import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisDetailResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponseV2;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.usecase.DiagnosisUseCase;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.repository.DiagnosisJpaRepository;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.RuleChainUseCase;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 청약 진단 서비스
 * - 단계적 파이프라인(기초자격 → 지역/통장 → 소득/자산 → 특별공급별 규칙 → 일반공급/가점 → 우선순위 산정)
 * - 규칙(Rule) 인터페이스 + 체인(Chain of Responsibility)
 * - 정책(Policy) 외부화: Threshold들을 Provider에서 주입(향후 YAML/DB/어드민에서 변경 가능)
 * - Explainable Result: 모든 통과/실패 사유를 코드 + 메시지 + 세부값으로 축적하여 응답으로 반환
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DiagnosisService implements DiagnosisUseCase {

    /// 의존성
    private final DiagnosisJpaRepository repository;

    /// 외부 의존성
    private final UserUseCase userService;
    private final RuleChainUseCase ruleChain;

    /**
     * 임대주택 진단하기
     * @param userId    진단할 유저
     * @param request   요청 DTO
     * @return          진단 DTO
     */
    @Override
    public DiagnosisResponse diagnose(UUID userId, DiagnosisRequest request) {

        /// 유저 예외 처리
        User user = userService.loadUser(userId);

        /// 진단 도메인 생성
        var diagnosis = Diagnosis.of(user, request);
        Diagnosis entity = repository.save(diagnosis);

        /// 작성한 내용을 바탕으로 진단 실행
        EvaluationContext context = ruleChain.evaluateAll(entity);

        /// DTO 생성
        return DiagnosisResponse.from(context);
    }

    @Override
    @Transactional
    public DiagnosisResponseV2 diagnoseV2(UUID userId, DiagnosisRequest request) {
        User user = userService.loadUser(userId);
        var diagnosis = Diagnosis.of(user, request);
        Diagnosis entity = repository.save(diagnosis);

        EvaluationContext context = ruleChain.evaluateAll(entity);
        return DiagnosisResponseV2.from(context);
    }

    /**
     * 나의 최근 청약진단 상세 조회 (입력 정보 + 결과)
     * @param userId    유저ID
     * @return          청약진단 상세 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public DiagnosisDetailResponse getDiagnoseDetail(UUID userId) {

        /// 유저 예외 처리
        User user = userService.loadUser(userId);

        /// DB에서 최근 진단 1개 조회
        Diagnosis diagnosis = repository.findTopByUserOrderByCreatedAtDesc(user)
                .orElse(null);

        /// 진단 기록이 없는 경우
        if (diagnosis == null) {
            return null;
        }

        /// 작성한 내용을 바탕으로 진단 실행 (규칙에 따라서 바뀔 수 있기에 매번 실행하도록 수정)
        EvaluationContext context = ruleChain.evaluateAll(diagnosis);

        /// DTO 생성
        return DiagnosisDetailResponse.from(context);
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisResponseV2 getDiagnoseSummaryV2(UUID userId) {
        User user = userService.loadUser(userId);

        Diagnosis diagnosis = repository.findTopByUserOrderByCreatedAtDesc(user)
                .orElse(null);

        if (diagnosis == null) {
            return null;
        }

        EvaluationContext context = ruleChain.evaluateAll(diagnosis);
        return DiagnosisResponseV2.from(context);
    }

}
