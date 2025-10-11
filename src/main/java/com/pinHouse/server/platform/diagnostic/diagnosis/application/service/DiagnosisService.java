package com.pinHouse.server.platform.diagnostic.diagnosis.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisRequest;
import com.pinHouse.server.platform.diagnostic.diagnosis.application.dto.DiagnosisResponse;
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

import java.util.NoSuchElementException;
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
        User user = getUser(userId);

        /// 진단 도메인 생성
        var diagnosis = Diagnosis.of(user, request);
        Diagnosis entity = repository.save(diagnosis);

        /// 작성한 내용을 바탕으로 진단 실행
        EvaluationContext context = ruleChain.evaluateAll(entity);

        /// DTO 생성
        return DiagnosisResponse.from(context);
    }

    /**
     * 나의 최근 청약진단 가져오기
     * @param userId    유저ID
     * @return          청약진단 DTO
     */
    @Override
    public DiagnosisResponse getDiagnose(UUID userId) {

        /// 유저 예외 처리
        User user = getUser(userId);

        /// DB에서 결과 조회하기, 영속성 컨텍스트에 저장
        Diagnosis diagnosis = repository.findByUser(user);

        /// 작성한 내용을 바탕으로 진단 실행 (규칙에 따라서 바뀔 수 있기에 매번 실행하도록 수정)
        EvaluationContext context = ruleChain.evaluateAll(diagnosis);

        /// DTO 생성
        return DiagnosisResponse.from(context);
    }


    /**
     * 나의 진단 목록 결과하기
     * @param userId    유저 ID
     */
    private User getUser(UUID userId) {
        return userService.loadUserById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }


}
