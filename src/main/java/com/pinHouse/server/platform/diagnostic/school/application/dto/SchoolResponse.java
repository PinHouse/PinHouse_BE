package com.pinHouse.server.platform.diagnostic.school.application.dto;

import com.pinHouse.server.platform.diagnostic.school.domain.entity.School;
import com.pinHouse.server.platform.diagnostic.school.domain.entity.University;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Schema(name = "[응답][학교] 학교 정보 조회 Response", description = "학교 정보를 반환하는 DTO입니다.")
public record SchoolResponse(
        @Schema(description = "학교 아이디", example = "101")
        String id,

        @Schema(description = "학교 이름", example = "서울대학교")
        String name,

        @Schema(description = "캠퍼스 유형", example = "도심형")
        String campusType,

        @Schema(description = "대학 유형", example = "종합대학교")
        String collegeType
) {

    /// 정적 팩토리 메서드
    public static SchoolResponse from(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .name(school.getSchoolName())
                .campusType(school.getSchoolType())
                .collegeType(school.getSchoolCategory())
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<SchoolResponse> from(List<School> schools) {
        return schools.stream()
                .map(SchoolResponse::from)
                .collect(Collectors.toList());
    }


    /// 정적 팩토리 메서드
    public static SchoolResponse fromUniv(University univ) {
        return SchoolResponse.builder()
                .id(univ.getId())
                .name(univ.getSchoolName())
                .campusType(univ.getCampusType())
                .collegeType(univ.getCollegeType())
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<SchoolResponse> fromUniv(List<University> univs) {
        return univs.stream()
                .map(SchoolResponse::fromUniv)
                .collect(Collectors.toList());
    }

}
