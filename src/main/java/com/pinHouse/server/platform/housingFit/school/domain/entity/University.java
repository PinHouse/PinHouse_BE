package com.pinHouse.server.platform.housingFit.school.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class University {

    private Long id;

    private String schoolName;

    private String campusType;

    private String collegeType;

    private Integer sidoCode;

    private String sidoName;

    private String sigunguName;
}
