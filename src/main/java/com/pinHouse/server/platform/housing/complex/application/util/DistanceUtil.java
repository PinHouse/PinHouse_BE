package com.pinHouse.server.platform.housing.complex.application.util;

import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * [외부 거리계산]을 위한 인터페이스입니다.
 */
public interface DistanceUtil {

    /// 응답
    PathResult findPathResult(double startY, double startX, double endY, double endX)
            throws UnsupportedEncodingException;


    /// 기존 코드
    default List<RootResult> findPath(double startY, double startX, double endY, double endX)
            throws UnsupportedEncodingException {
        return findPathResult(startY, startX, endY, endX).routes();
    }
}
