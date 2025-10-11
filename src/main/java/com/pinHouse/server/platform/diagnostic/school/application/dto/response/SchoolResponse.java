package com.pinHouse.server.platform.diagnostic.school.application.dto.response;

import com.pinHouse.server.platform.diagnostic.school.domain.entity.School;
import com.pinHouse.server.platform.diagnostic.school.domain.entity.University;
import lombok.Builder;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record SchoolResponse(
        String id,
        String name,
        String campusType,
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
