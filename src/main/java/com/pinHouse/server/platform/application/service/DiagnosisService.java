package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.platform.application.in.diagnosis.DiagnosisUseCase;
import com.pinHouse.server.platform.application.out.diagnosis.DiagnosisPort;
import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisResult;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiagnosisService implements DiagnosisUseCase {

    private final DiagnosisPort port;

    @Override
    public DiagnosisResult diagnose(DiagnosisRequest request) {

        /// 도메인 변환
        Diagnosis domain = request.toDomain();

        /// DB에 객체 저장.
        port.saveDiagnosis(domain);

        /// 무주택 여부
        if (!request.isHomeless()) {
            return DiagnosisResult.of(false, "불가", "무주택자가 아니므로 신청 불가");
        }

        /// 가구원 수
        int familyCount = request.getFamilyCount();

        /// 소득 수준
        double incomeRatio = request.getIncomeRatio();
        if (incomeRatio > 150.0) {
            return DiagnosisResult.of(false, "불가", "소득 기준 초과");
        }

        /// 연령, 혼인, 부양, 특수 대상자
        if (request.getAge() >= 18 && request.getAge() <= 39 && "미혼".equals(request.getMaritalStatus())) {
            return DiagnosisResult.of(true, SupplyType.YOUTH_SPECIAL.getValue(), "청년 특별공급");
        }
        if ("신혼부부".equals(request.getMaritalStatus())) {
            return DiagnosisResult.of(true, SupplyType.NEWCOUPLE_SPECIAL.getValue(), "신혼부부 특별공급");
        }
        if (request.getAge() >= 65) {
            return DiagnosisResult.of(true, SupplyType.ELDER_SPECIAL.getValue(), "고령자 특별공급");
        }
        if (request.getMinorChildrenCount() >= 2) {
            return DiagnosisResult.of(true, SupplyType.MULTICHILD_SPECIAL.getValue(), "다자녀 특별공급");
        }
        if (request.isHasElderDependent()) {
            return DiagnosisResult.of(true, SupplyType.ELDER_SUPPORT_SPECIAL.getValue(), "65세 이상 직계존속 부양");
        }
        if (request.isSpecialTarget()) {
            return DiagnosisResult.of(true, SupplyType.SPECIAL.getValue(), "특수 대상자");
        }

        // 기본 일반공급
        return DiagnosisResult.of(true, SupplyType.GENERAL.name(), "일반공급");
    }
}
