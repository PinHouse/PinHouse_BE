package com.pinHouse.server.platform.diagnostic.school.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "university")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class University {

    @Id
    private String id;

    private String schoolName;

    private String campusType;

    private String collegeType;

    private Integer sidoCode;

    private String sidoName;

    private String sigunguName;

    /// 빌더 생성자
    @Builder
    public University(String schoolName, String campusType, String collegeType, Integer sidoCode, String sidoName, String sigunguName) {
        this.id = UUID.randomUUID().toString();
        this.schoolName = schoolName;
        this.campusType = campusType;
        this.collegeType = collegeType;
        this.sidoCode = sidoCode;
        this.sidoName = sidoName;
        this.sigunguName = sigunguName;
    }
}
