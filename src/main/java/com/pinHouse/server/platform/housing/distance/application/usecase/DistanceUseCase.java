package com.pinHouse.server.platform.housing.distance.application.usecase;

import com.pinHouse.server.platform.housing.distance.application.dto.response.DistanceResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * [외부 거리계산]을 위한 인터페이스입니다.
 *
 */
public interface DistanceUseCase {

    // 출발지의 위도와 도착지의 위도를 바탕으로 계산
    List<DistanceResponse> findPath(double startY, double startX, double endY, double endX) throws UnsupportedEncodingException;

}
