package com.pinHouse.server.platform.housing.complex.application.util;

import com.pinHouse.server.platform.housing.complex.application.dto.response.ChipType;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 교통수단 색상 추출 유틸리티 클래스
 * 교통수단 타입과 노선 정보를 바탕으로 배경 색상(Hex)을 추출합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransportColorResolver {

    /** 기본 색상 (회색) */
    private static final String DEFAULT_COLOR = "#BBBBBB";

    /**
     * enum으로부터 배경 색상(Hex) 추출
     *
     * @param step 교통 단계 정보
     * @param type 교통수단 타입
     * @return 배경 색상(Hex 코드)
     */
    public static String extractBgColorHex(RootResult.DistanceStep step, ChipType type) {
        if (step == null || type == null) {
            return DEFAULT_COLOR;
        }

        // WALK와 AIR는 ChipType의 defaultBg 사용
        if (type == ChipType.WALK || type == ChipType.AIR) {
            return type.defaultBg;
        }

        // SUBWAY: subwayLine enum의 색상 사용
        if (type == ChipType.SUBWAY) {
            if (step.subwayLine() != null) {
                return step.subwayLine().getColorHex();
            }
            return DEFAULT_COLOR;
        }

        // BUS: busRouteType 또는 expressBusType enum의 색상 사용
        if (type == ChipType.BUS) {
            if (step.busRouteType() != null) {
                return step.busRouteType().getColorHex();
            }
            if (step.expressBusType() != null) {
                return step.expressBusType().getColorHex();
            }
            return DEFAULT_COLOR;
        }

        // TRAIN: trainType enum의 색상 사용
        if (type == ChipType.TRAIN) {
            if (step.trainType() != null) {
                return step.trainType().getColorHex();
            }
            return DEFAULT_COLOR;
        }

        // 그 외의 경우 기본 회색 반환
        return DEFAULT_COLOR;
    }
}
