package com.pinHouse.server.platform.adapter.in.web.dto.request;

import com.pinHouse.server.platform.domain.diagnosis.entity.MaritalStatus;
import com.pinHouse.server.platform.domain.diagnosis.model.DiagnosisRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DiagnosisRequestDTO {

    @Schema(description = "무주택 여부", example = "true")
    private boolean isHomeless;

    @Schema(description = "세대원 수", example = "3")
    private int familyCount;

    // 2. 소득 관련
    @Schema(description = "중위소득 대비 비율(%)", example = "85.5")
    private double incomeRatio;

    // 3. 자산 관련
    @Schema(description = "부동산, 금융자산 가치 (만원 단위)", example = "12000.0")
    private double assetValue;

    @Schema(description = "자동차 가액 (만원 단위)", example = "1800.0")
    private double carValue;

    // 4. 연령 및 혼인 상태
    @Schema(description = "만 나이", example = "29")
    private int age;

    @Schema(description = "혼인 상태", example = "미혼")
    private MaritalStatus maritalStatus;

    // 5. 부양가족 유무
    @Schema(description = "미성년 자녀 수", example = "1")
    private int minorChildrenCount;

    @Schema(description = "65세 이상 직계존속 부양 여부", example = "false")
    private boolean hasElderDependent;

    // 6. 특수 대상자 여부
    @Schema(description = "특수 대상자 여부(국가유공자, 장애인, 북한이탈주민 등)", example = "true")
    private boolean isSpecialTarget;



    /// 정적 팩토리 메서드
    public DiagnosisRequest toDomain() {
        return DiagnosisRequest.builder()
                .isHomeless(isHomeless)
                .familyCount(familyCount)
                .incomeRatio(incomeRatio)
                .assetValue(assetValue)
                .carValue(carValue)
                .age(age)
                .maritalStatus(maritalStatus)
                .minorChildrenCount(minorChildrenCount)
                .hasElderDependent(hasElderDependent)
                .isSpecialTarget(isSpecialTarget)
                .build();
    }

}
