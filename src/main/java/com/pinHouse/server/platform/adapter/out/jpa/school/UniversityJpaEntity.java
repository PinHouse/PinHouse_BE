package com.pinHouse.server.platform.adapter.out.jpa.school;

import com.pinHouse.server.platform.domain.school.University;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "university")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UniversityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;

    private String campusType;

    private String collegeType;

    private Integer sidoCode;

    private String sidoName;

    private String sigunguName;

    /// 정적 팩토리 메서드
    public static UniversityJpaEntity from(University university) {
        return UniversityJpaEntity.builder()
                .schoolName(university.getSchoolName())
                .campusType(university.getCampusType())
                .collegeType(university.getCollegeType())
                .sidoCode(university.getSidoCode())
                .sidoName(university.getSidoName())
                .sigunguName(university.getSigunguName())
                .build();
    }

    /// 도메인으로 변환
    public University toDomain() {
        return University.builder()
                .id(id)
                .schoolName(schoolName)
                .campusType(campusType)
                .collegeType(collegeType)
                .sidoCode(sidoCode)
                .sidoName(sidoName)
                .sigunguName(sigunguName)
                .build();

    }

}
