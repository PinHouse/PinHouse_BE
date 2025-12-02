package com.pinHouse.server.platform.housing.complex.application.dto.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("교통 수단 Enum 테스트")
class TransportEnumTest {

    @Test
    @DisplayName("TransportType - trafficType 코드로부터 변환 테스트")
    void testTransportTypeFromTrafficType() {
        assertThat(RootResult.TransportType.fromTrafficType(1)).isEqualTo(RootResult.TransportType.SUBWAY);
        assertThat(RootResult.TransportType.fromTrafficType(2)).isEqualTo(RootResult.TransportType.BUS);
        assertThat(RootResult.TransportType.fromTrafficType(3)).isEqualTo(RootResult.TransportType.WALK);
        assertThat(RootResult.TransportType.fromTrafficType(4)).isEqualTo(RootResult.TransportType.TRAIN);
        assertThat(RootResult.TransportType.fromTrafficType(5)).isEqualTo(RootResult.TransportType.BUS); // 고속버스
        assertThat(RootResult.TransportType.fromTrafficType(6)).isEqualTo(RootResult.TransportType.BUS); // 시외버스
        assertThat(RootResult.TransportType.fromTrafficType(7)).isEqualTo(RootResult.TransportType.AIR);
        assertThat(RootResult.TransportType.fromTrafficType(999)).isEqualTo(RootResult.TransportType.UNKNOWN);
    }

    @Test
    @DisplayName("SubwayLineType - 숫자 코드로부터 변환 테스트")
    void testSubwayLineTypeFromInt() {
        assertThat(SubwayLineType.from(1)).isEqualTo(SubwayLineType.SEOUL_LINE_1);
        assertThat(SubwayLineType.from(2)).isEqualTo(SubwayLineType.SEOUL_LINE_2);
        assertThat(SubwayLineType.from(91)).isEqualTo(SubwayLineType.GTX_A);
        assertThat(SubwayLineType.from(21)).isEqualTo(SubwayLineType.INCHEON_LINE_1);
        assertThat(SubwayLineType.from(71)).isEqualTo(SubwayLineType.BUSAN_LINE_1);
        assertThat(SubwayLineType.from(999)).isEqualTo(SubwayLineType.UNKNOWN);
    }

    @Test
    @DisplayName("SubwayLineType - 문자열 코드로부터 변환 테스트")
    void testSubwayLineTypeFromString() {
        assertThat(SubwayLineType.from("1")).isEqualTo(SubwayLineType.SEOUL_LINE_1);
        assertThat(SubwayLineType.from("91")).isEqualTo(SubwayLineType.GTX_A);
        assertThat(SubwayLineType.from("114")).isEqualTo(SubwayLineType.SEOHAE);
        assertThat(SubwayLineType.from(null)).isEqualTo(SubwayLineType.UNKNOWN);
        assertThat(SubwayLineType.from("")).isEqualTo(SubwayLineType.UNKNOWN);
        assertThat(SubwayLineType.from("invalid")).isEqualTo(SubwayLineType.UNKNOWN);
    }

    @Test
    @DisplayName("SubwayLineType - 노선명 확인 테스트")
    void testSubwayLineTypeName() {
        assertThat(SubwayLineType.SEOUL_LINE_1.getName()).isEqualTo("수도권 1호선");
        assertThat(SubwayLineType.GTX_A.getName()).isEqualTo("GTX-A");
        assertThat(SubwayLineType.BUSAN_LINE_1.getName()).isEqualTo("부산 1호선");
    }

    @Test
    @DisplayName("BusRouteType - 숫자 코드로부터 변환 테스트")
    void testBusRouteTypeFromInt() {
        assertThat(BusRouteType.from(1)).isEqualTo(BusRouteType.GENERAL);
        assertThat(BusRouteType.from(2)).isEqualTo(BusRouteType.SEAT);
        assertThat(BusRouteType.from(3)).isEqualTo(BusRouteType.VILLAGE);
        assertThat(BusRouteType.from(4)).isEqualTo(BusRouteType.DIRECT_SEAT);
        assertThat(BusRouteType.from(11)).isEqualTo(BusRouteType.TRUNK_LINE);
        assertThat(BusRouteType.from(999)).isEqualTo(BusRouteType.UNKNOWN);
    }

    @Test
    @DisplayName("BusRouteType - 문자열 코드로부터 변환 테스트")
    void testBusRouteTypeFromString() {
        assertThat(BusRouteType.from("1")).isEqualTo(BusRouteType.GENERAL);
        assertThat(BusRouteType.from("4")).isEqualTo(BusRouteType.DIRECT_SEAT);
        assertThat(BusRouteType.from(null)).isEqualTo(BusRouteType.UNKNOWN);
        assertThat(BusRouteType.from("")).isEqualTo(BusRouteType.UNKNOWN);
        assertThat(BusRouteType.from("invalid")).isEqualTo(BusRouteType.UNKNOWN);
    }

    @Test
    @DisplayName("BusRouteType - 레이블 확인 테스트")
    void testBusRouteTypeLabel() {
        assertThat(BusRouteType.GENERAL.getLabel()).isEqualTo("일반");
        assertThat(BusRouteType.DIRECT_SEAT.getLabel()).isEqualTo("직행좌석");
        assertThat(BusRouteType.TRUNK_LINE.getLabel()).isEqualTo("간선");
    }

    @Test
    @DisplayName("TrainType - 숫자 코드로부터 변환 테스트")
    void testTrainTypeFromInt() {
        assertThat(TrainType.from(1)).isEqualTo(TrainType.KTX);
        assertThat(TrainType.from(2)).isEqualTo(TrainType.SAEMAUL);
        assertThat(TrainType.from(3)).isEqualTo(TrainType.MUGUNHWA);
        assertThat(TrainType.from(6)).isEqualTo(TrainType.ITX);
        assertThat(TrainType.from(8)).isEqualTo(TrainType.SRT);
        assertThat(TrainType.from(999)).isEqualTo(TrainType.UNKNOWN);
        assertThat(TrainType.from(-1)).isEqualTo(TrainType.UNKNOWN);
    }

    @Test
    @DisplayName("TrainType - 레이블 확인 테스트")
    void testTrainTypeLabel() {
        assertThat(TrainType.KTX.getLabel()).isEqualTo("KTX");
        assertThat(TrainType.SAEMAUL.getLabel()).isEqualTo("새마을");
        assertThat(TrainType.SRT.getLabel()).isEqualTo("SRT");
        assertThat(TrainType.UNKNOWN.getLabel()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("ExpressBusType - 숫자 코드로부터 변환 테스트")
    void testExpressBusTypeFromInt() {
        assertThat(ExpressBusType.from(1)).isEqualTo(ExpressBusType.GENERAL);
        assertThat(ExpressBusType.from(2)).isEqualTo(ExpressBusType.PREMIUM);
        assertThat(ExpressBusType.from(3)).isEqualTo(ExpressBusType.SUPER_PREMIUM);
        assertThat(ExpressBusType.from(6)).isEqualTo(ExpressBusType.NIGHT_SUPER_PREMIUM);
        assertThat(ExpressBusType.from(7)).isEqualTo(ExpressBusType.WEEKEND_PREMIUM);
        assertThat(ExpressBusType.from(999)).isEqualTo(ExpressBusType.UNKNOWN);
    }

    @Test
    @DisplayName("ExpressBusType - 레이블 확인 테스트")
    void testExpressBusTypeLabel() {
        assertThat(ExpressBusType.GENERAL.getLabel()).isEqualTo("일반");
        assertThat(ExpressBusType.PREMIUM.getLabel()).isEqualTo("우등");
        assertThat(ExpressBusType.SUPER_PREMIUM.getLabel()).isEqualTo("프리미엄");
    }
}
