package com.pinHouse.server.platform.domain.facility;

import com.pinHouse.server.platform.domain.location.Location;

public interface Facility {

    /**
     * 시설의 위치 정보를 반환합니다.
     * @return 시설 위치 객체
     */
    Location getLocation();

}
