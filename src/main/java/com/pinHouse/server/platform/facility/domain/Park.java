package com.pinHouse.server.platform.facility.domain;

import com.pinHouse.server.platform.location.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Park implements Facility{

    /// 아이디
    private String id;

    /// 공원 시스템 ID
    private String parkId;

    /// 명칭
    private String name;

    /// 공원 분류 명칭
    private String category;

    /// PNU (법정동+지번코드)
    private String pnu;

    /// 좌표
    private Location location;


}
