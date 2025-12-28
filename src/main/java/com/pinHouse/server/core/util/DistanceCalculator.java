package com.pinHouse.server.core.util;

import com.pinHouse.server.platform.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 거리 계산 유틸리티 클래스
 * Haversine 공식을 사용하여 두 지점 간의 거리를 계산합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DistanceCalculator {

    /** 지구 반지름 (km) */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 두 지점 간 거리(km) 계산 (Haversine 공식)
     *
     * @param location1 첫 번째 위치 (위도, 경도)
     * @param location2 두 번째 위치 (위도, 경도)
     * @return 두 지점 간의 거리 (km)
     */
    public static double calculateDistanceKm(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        double lat1 = location1.getLatitude();
        double lon1 = location1.getLongitude();
        double lat2 = location2.getLatitude();
        double lon2 = location2.getLongitude();

        return calculateDistanceKm(lat1, lon1, lat2, lon2);
    }

    /**
     * 두 지점 간 거리(km) 계산 (Haversine 공식)
     *
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (km)
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(radLat1) * Math.cos(radLat2);

        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }
}
