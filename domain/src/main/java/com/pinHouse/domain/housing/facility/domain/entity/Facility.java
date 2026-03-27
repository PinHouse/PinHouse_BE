package com.pinHouse.domain.housing.facility.domain.entity;

import com.pinHouse.domain.Location;

public interface Facility {

    /**
     * 시설의 위치 정보를 반환합니다.
     * @return 시설 위치 객체
     */
    Location getLocation();

}
