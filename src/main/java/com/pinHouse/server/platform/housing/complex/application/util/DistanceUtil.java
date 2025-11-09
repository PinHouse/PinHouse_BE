package com.pinHouse.server.platform.housing.complex.application.util;

import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;

import java.io.UnsupportedEncodingException;

/**
 * [외부 거리계산]을 위한 인터페이스입니다.
 */
public interface DistanceUtil {

    /// 응답
    PathResult findPathResult(double startY, double startX, double endY, double endX)
            throws UnsupportedEncodingException;

}
