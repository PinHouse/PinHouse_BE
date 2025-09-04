package com.pinHouse.server.platform.adapter.out.jpa.school;

import com.pinHouse.server.platform.domain.school.School;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "school")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SchoolJpaEntity {

    @Id
    private String id;

    private String schoolName;

    private String schoolType;

    private String schoolCategory;

    private String sidoCode;

    private String sidoName;

    private String sigunguName;

    /// 정적 팩토리 메서드
    public static SchoolJpaEntity from(School school) {
        return SchoolJpaEntity.builder()
                .id(school.getId())
                .schoolName(school.getSchoolName())
                .schoolType(school.getSchoolType())
                .schoolCategory(school.getSchoolCategory())
                .sidoCode(school.getSidoCode())
                .sidoName(school.getSidoName())
                .sigunguName(school.getSigunguName())
                .build();
    }


    /// 도메인 변환 메서드
    public School toDomain() {
        return School.builder()
                .id(getId())
                .schoolName(getSchoolName())
                .schoolType(getSchoolType())
                .schoolCategory(getSchoolCategory())
                .sidoCode(getSidoCode())
                .sidoName(getSidoName())
                .sigunguName(getSigunguName())
                .build();
    }




}
