package com.myhome.server.platform.domain.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeComplex {

    private String name;

    private String address;

    private String area;

    private String totalHouseholds;

    private String heatingMethod;

    private String expectedMoveInMonth;

}
