package com.pinHouse.server.platform.housing.facility.domain.entity.infra;

import com.pinHouse.server.core.entity.Location;

public interface Facility {

    /**
     * 시설의 위치 정보를 반환합니다.
     * @return 시설 위치 객체
     */
    Location getLocation();

}
