package com.pinHouse.server.platform.housing.complex.application.util;

import com.pinHouse.server.platform.housing.complex.application.dto.response.ChipType;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransitResponseMapper {

    // =================
    //  퍼블릭 로직
    // =================

    /// 적절한 1개 선택
    public RootResult selectBest(PathResult pathResult) {

        /// 없으면 null
        if (pathResult == null) {
            return null;
        }

        /// 1순위 최소 운행 시간, 2순위 금액 기준으로 표기
        return pathResult.routes().stream()
                .min(Comparator.comparingInt(RootResult::totalTime)
                        .thenComparingInt(RootResult::totalPayment))
                .orElse(null);
    }

    /// 적절한 1개 선택
    public List<RootResult> selectTop3(PathResult pathResult) {

        /// 없으면 null
        if (pathResult == null || pathResult.routes() == null) {
            return List.of();
        }

        /// 1순위 최소 운행 시간, 2순위 금액 기준으로 표기
        return pathResult.routes().stream()
                .sorted(Comparator.comparingInt(RootResult::totalTime)
                        .thenComparingInt(RootResult::totalPayment)) // 시간 → 요금 순 정렬
                .limit(3) // 상위 3개만 선택
                .toList();
    }

    public List<DistanceResponse.TransferPointResponse> extractStops(RootResult route) {

        List<DistanceResponse.TransferPointResponse> result = new ArrayList<>();

        if (route == null || route.steps() == null || route.steps().isEmpty()) {
            return result;
        }

        // WALK 빼고 “실제 탑승하는 구간”만 추출
        List<RootResult.DistanceStep> moveSteps = route.steps().stream()
                .filter(s -> s.type() != RootResult.TransportType.WALK)
                .toList();

        if (moveSteps.isEmpty()) {
            return result;
        }

        // 1) 첫 승차 지점
        RootResult.DistanceStep first = moveSteps.get(0);
        result.add(DistanceResponse.TransferPointResponse.builder()
                .role(DistanceResponse.TransferPointResponse.TransferRole.START)
                .type(mapType(first.type()))                 // 기존 mapType 재사용
                .stopName(first.startName())                // 승차 지점
                .lineText(first.lineInfo())                 // 버스번호/호선명
                .build());

        // 2) 중간 환승 지점들 (두 번째 탑승부터는 전부 환승으로 간주)
        for (int i = 1; i < moveSteps.size(); i++) {
            RootResult.DistanceStep step = moveSteps.get(i);

            result.add(DistanceResponse.TransferPointResponse.builder()
                    .role(DistanceResponse.TransferPointResponse.TransferRole.TRANSFER)
                    .type(mapType(step.type()))
                    .stopName(step.startName())            // 환승 지점 이름
                    .lineText(step.lineInfo())             // 환승 후 타는 노선
                    .build());
        }

        // 3) 마지막 도착 지점
        RootResult.DistanceStep last = moveSteps.get(moveSteps.size() - 1);
        result.add(DistanceResponse.TransferPointResponse.builder()
                .role(DistanceResponse.TransferPointResponse.TransferRole.ARRIVAL)
                .type(mapType(last.type()))
                .stopName(last.endName())                  // 최종 도착 지점
                .lineText(null)                            // 필요 없으면 null
                .build());

        return result;
    }



    /// 출력을 위한 매퍼
    public List<DistanceResponse.TransitResponse> from(RootResult route) {

        /// 출력할 값 리스트
        List<DistanceResponse.TransitResponse> chips = new ArrayList<>();

        /// 없으면 Null
        if (route == null || route.steps() == null) {
            return chips;
        }

        /// 내부 로직
        route.steps().forEach(step -> {

            /// enum
            ChipType type = mapType(step.type());

            /// 분
            String minutes = formatMinutes(step.time());

            /// 라인
            String line = normalizeLine(step, type);

            chips.add(DistanceResponse.TransitResponse.builder()
                    .type(type)
                    .minutesText(minutes)
                    .lineText(line)
                    .bgColorHex(type.defaultBg)
                    .build());
        });

        // 인접 동일 타입+동일 라인 병합을 원하면 아래 주석 해제하여 merge
        // return mergeSameTypeAndLine(chips);
        return chips;
    }

    // =================
    //  내부 로직
    // =================

    /// Enum 매핑
    private static ChipType mapType(RootResult.TransportType t) {

        /// 없으면 걷기
        if (t == null) {
            return ChipType.WALK;
        }

        /// 있으면
        return switch (t) {
            case WALK -> ChipType.WALK;
            case BUS -> ChipType.BUS;
            case SUBWAY -> ChipType.SUBWAY;
            case TRAIN -> ChipType.TRAIN;
            case AIR -> ChipType.AIR;
        };
    }

    /// 분 표기
    private static String formatMinutes(int min) {

        /// 방어
        if (min <= 0){
            return "0분";
        }

        /// 리턴
        return min + "분";
    }

    /** 버스/지하철 표기 보정 */
    private static String normalizeLine(RootResult.DistanceStep step, ChipType type) {

        /// 라인
        String line = step.lineInfo();
        if (type == ChipType.WALK) {
            return null;
        }

        /// 지하철 처리
        if (type == ChipType.SUBWAY && line != null && !line.isBlank()) {

            /// 이미 "호선" 포함이면 그대로, 아니면 "호선" 접미
            if (!line.endsWith("호선") && line.chars().allMatch(Character::isDigit)) {
                return line + "호선";
            }
            return line;
        }

        /// 나머지는 그대로
        return (line == null || line.isBlank()) ? null : line;
    }
}
