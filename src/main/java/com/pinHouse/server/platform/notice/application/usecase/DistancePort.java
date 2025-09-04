package com.pinHouse.server.platform.notice.application.usecase;

import java.io.UnsupportedEncodingException;

/**
 * [외부 거리계산]을 위한 인터페이스입니다.
 *
 */
public interface DistancePort {

    // 출발지의 위도와 도착지의 위도를 바탕으로 계산
    String findPath(double startY, double startX, double endY, double endX) throws UnsupportedEncodingException;

}
