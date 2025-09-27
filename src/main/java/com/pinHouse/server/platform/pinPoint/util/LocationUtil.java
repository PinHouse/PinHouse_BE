package com.pinHouse.server.platform.pinPoint.util;

import com.pinHouse.server.platform.Location;

/**
 * 좌표 변환을 위한 인터페이스
 */
public interface LocationUtil {

    /// 주소명을 넣으면 좌표로 변환
    Location getLocation(String address);


}
