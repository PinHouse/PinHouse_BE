package com.pinHouse.server.platform.housing.complex.application.util;

import com.pinHouse.server.core.util.TimeFormatter;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ChipType;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.TransitRoutesResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.application.dto.result.RootResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 대중교통 경로 응답 매퍼
 *
 * <p>ODsay API의 {@link PathResult}와 {@link RootResult}를
 * 애플리케이션의 응답 DTO로 변환하는 역할을 담당합니다.</p>
 *
 * <h3>지원하는 스키마</h3>
 * <ul>
 *   <li><b>신규 스키마:</b> {@link TransitRoutesResponse} - 3개 경로 + 상세 단계 정보</li>
 *   <li><b>구 스키마 (Deprecated):</b> {@link DistanceResponse} - 단일 경로 정보</li>
 * </ul>
 *
 * <h3>주요 기능</h3>
 * <ul>
 *   <li>경로 선택 (최적 1개, 상위 3개)</li>
 *   <li>교통수단 타입 매핑 및 색상 추출</li>
 *   <li>노선 정보 정규화</li>
 *   <li>승차/하차/도보 단계별 상세 정보 생성</li>
 * </ul>
 */
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

    /**
     * 환승 지점 추출 (구 스키마)
     *
     * @deprecated 이 메서드는 구식 스키마에서만 사용됩니다.
     *             새 스키마에서는 {@link #toStepResponses(RootResult)}를 사용하세요.
     *             이 메서드는 향후 버전에서 제거될 예정입니다.
     */
    @Deprecated(since = "1.0", forRemoval = true)
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
        ChipType firstType = mapType(first.type());
        result.add(DistanceResponse.TransferPointResponse.builder()
                .role(DistanceResponse.TransferPointResponse.TransferRole.START)
                .type(firstType)
                .stopName(first.startName())
                .lineText(first.lineInfo())
                .line(first.line())
                .subwayLine(first.subwayLine())
                .busRouteType(first.busRouteType())
                .trainType(first.trainType())
                .expressBusType(first.expressBusType())
                .bgColorHex(TransportColorResolver.extractBgColorHex(first, firstType))
                .build());

        // 2) 중간 환승 지점들 (두 번째 탑승부터는 전부 환승으로 간주)
        for (int i = 1; i < moveSteps.size(); i++) {
            RootResult.DistanceStep step = moveSteps.get(i);
            ChipType stepType = mapType(step.type());

            result.add(DistanceResponse.TransferPointResponse.builder()
                    .role(DistanceResponse.TransferPointResponse.TransferRole.TRANSFER)
                    .type(stepType)
                    .stopName(step.startName())
                    .lineText(step.lineInfo())
                    .line(step.line())
                    .subwayLine(step.subwayLine())
                    .busRouteType(step.busRouteType())
                    .trainType(step.trainType())
                    .expressBusType(step.expressBusType())
                    .bgColorHex(TransportColorResolver.extractBgColorHex(step, stepType))
                    .build());
        }

        // 3) 마지막 도착 지점
        RootResult.DistanceStep last = moveSteps.get(moveSteps.size() - 1);
        ChipType lastType = mapType(last.type());
        result.add(DistanceResponse.TransferPointResponse.builder()
                .role(DistanceResponse.TransferPointResponse.TransferRole.ARRIVAL)
                .type(lastType)
                .stopName(last.endName())
                .lineText(null)
                .line(null)
                .subwayLine(null)
                .busRouteType(null)
                .trainType(null)
                .expressBusType(null)
                .bgColorHex(null)
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
            String minutes = TimeFormatter.formatTime(step.time());

            /// 라인
            String line = normalizeLine(step, type);

            /// bgColorHex를 enum으로부터 추출
            String bgColorHex = TransportColorResolver.extractBgColorHex(step, type);

            chips.add(DistanceResponse.TransitResponse.builder()
                    .type(type)
                    .minutesText(minutes)
                    .lineText(line)
                    .line(step.line())
                    .subwayLine(step.subwayLine())
                    .busRouteType(step.busRouteType())
                    .trainType(step.trainType())
                    .expressBusType(step.expressBusType())
                    .bgColorHex(bgColorHex)
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
            case UNKNOWN -> ChipType.WALK; // UNKNOWN은 WALK로 처리
        };
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

    // =================
    //  새 스키마 변환 로직
    // =================

    /**
     * 새 스키마: 3개 경로를 한 번에 변환
     */
    public TransitRoutesResponse toTransitRoutesResponse(PathResult pathResult) {
        if (pathResult == null || pathResult.routes() == null) {
            return TransitRoutesResponse.builder()
                    .totalCount(0)
                    .routes(List.of())
                    .build();
        }

        List<RootResult> top3 = selectTop3(pathResult);
        List<TransitRoutesResponse.RouteResponse> routeResponses = new ArrayList<>();

        for (int i = 0; i < top3.size(); i++) {
            RootResult route = top3.get(i);
            routeResponses.add(toRouteResponse(route, i));
        }

        return TransitRoutesResponse.builder()
                .totalCount(routeResponses.size())
                .routes(routeResponses)
                .build();
    }

    /**
     * 개별 경로 변환
     */
    private TransitRoutesResponse.RouteResponse toRouteResponse(RootResult route, int index) {
        return TransitRoutesResponse.RouteResponse.builder()
                .routeIndex(index)
                .summary(toSummaryResponse(route))
                .distance(toSegmentResponses(route))
                .steps(toStepResponses(route))
                .build();
    }

    /**
     * 요약 정보 생성
     */
    private TransitRoutesResponse.SummaryResponse toSummaryResponse(RootResult route) {
        int totalMinutes = route.totalTime();
        int transferCount = countTransfers(route);

        return TransitRoutesResponse.SummaryResponse.builder()
                .totalMinutes(totalMinutes)
                .totalDistanceKm(Math.round(route.totalDistance() / 100.0) / 10.0)
                .totalFareWon(route.totalPayment() > 0 ? route.totalPayment() : null)
                .transferCount(transferCount)
                .displayText(TimeFormatter.formatTime(totalMinutes))
                .build();
    }

    /**
     * 환승 횟수 계산 (WALK 제외한 교통수단이 2개 이상이면 환승 발생)
     */
    private int countTransfers(RootResult route) {
        if (route == null || route.steps() == null) {
            return 0;
        }

        long transportCount = route.steps().stream()
                .filter(s -> s.type() != RootResult.TransportType.WALK)
                .count();

        return (int) Math.max(0, transportCount - 1);
    }

    /**
     * Segments 생성 (색 막대용)
     */
    public List<TransitRoutesResponse.SegmentResponse> toSegmentResponses(RootResult route) {
        if (route == null || route.steps() == null) {
            return List.of();
        }

        return route.steps().stream()
                .filter(step -> step.time() > 0)  // 0분인 구간은 제외
                .map(step -> {
                    ChipType type = mapType(step.type());
                    String bgColorHex = TransportColorResolver.extractBgColorHex(step, type);

                    // 버스일 때 노선 정보 포함
                    String minutesText;
                    if (step.type() == RootResult.TransportType.BUS && step.lineInfo() != null && !step.lineInfo().isBlank()) {
                        // 버스 노선 정보 포함
                        minutesText = step.lineInfo();
                    } else if (step.type() == RootResult.TransportType.SUBWAY && step.lineInfo() != null && !step.lineInfo().isBlank()) {
                        // 지하철 노선 정보 포함
                        String normalizedLine = normalizeLine(step, type);
                        minutesText = normalizedLine;
                    } else {
                        // 기타 교통수단은 시간만 표시
                        minutesText = TimeFormatter.formatTime(step.time());
                    }

                    return TransitRoutesResponse.SegmentResponse.builder()
                            .type(step.type().name())
                            .minutes(step.time())
                            .minutesText(minutesText)
                            .colorHex(bgColorHex)
                            .line(step.line())
                            .build();
                })
                .toList();
    }

    /**
     * Steps 생성 (색깔 + 승차/하차 통합)
     */
    private List<TransitRoutesResponse.StepResponse> toStepResponses(RootResult route) {
        if (route == null || route.steps() == null || route.steps().isEmpty()) {
            return List.of();
        }

        List<TransitRoutesResponse.StepResponse> steps = new ArrayList<>();
        List<RootResult.DistanceStep> distanceSteps = route.steps();

        // WALK를 제외한 실제 교통수단 구간
        List<RootResult.DistanceStep> transportSteps = distanceSteps.stream()
                .filter(s -> s.type() != RootResult.TransportType.WALK)
                .toList();

        if (transportSteps.isEmpty()) {
            // WALK만 있는 경로 (드문 케이스)
            for (RootResult.DistanceStep step : distanceSteps) {
                steps.add(createWalkStep(step, null));
            }
            return assignStepIndexes(steps);
        }

        // 1. DEPART (출발) 추가
        RootResult.DistanceStep firstTransport = transportSteps.get(0);
        steps.add(createDepartStep(firstTransport.startName()));

        // 2. 전체 구간 순회하며 steps 생성
        int transportIndex = 0;
        for (int i = 0; i < distanceSteps.size(); i++) {
            RootResult.DistanceStep step = distanceSteps.get(i);

            if (step.type() == RootResult.TransportType.WALK) {
                // WALK step 추가 (색상 포함)
                steps.add(createWalkStep(step, ChipType.WALK));
            } else {
                // 교통수단: BOARD + ALIGHT 추가 (색상 포함)
                ChipType chipType = mapType(step.type());
                steps.add(createBoardStep(step, chipType));

                // 다음이 WALK거나 마지막이면 하차
                boolean isLast = (transportIndex == transportSteps.size() - 1);
                if (isLast || (i + 1 < distanceSteps.size() && distanceSteps.get(i + 1).type() == RootResult.TransportType.WALK)) {
                    steps.add(createAlightStep(step, chipType));
                }

                transportIndex++;
            }
        }

        // 3. ARRIVE (도착) 추가
        RootResult.DistanceStep lastTransport = transportSteps.get(transportSteps.size() - 1);
        steps.add(createArriveStep(lastTransport.endName()));

        // 4. minutes가 0인 step 필터링 (DEPART/ARRIVE/ALIGHT는 null이므로 유지)
        List<TransitRoutesResponse.StepResponse> filteredSteps = steps.stream()
                .filter(step -> {
                    // minutes가 null이면 유지 (DEPART, ARRIVE, ALIGHT)
                    if (step.minutes() == null) {
                        return true;
                    }
                    // minutes가 0이면 제거, 0보다 크면 유지
                    return step.minutes() > 0;
                })
                .toList();

        return assignStepIndexes(filteredSteps);
    }

    /**
     * DEPART step 생성
     */
    private TransitRoutesResponse.StepResponse createDepartStep(String stopName) {
        return TransitRoutesResponse.StepResponse.builder()
                .stepIndex(0)
                .action(TransitRoutesResponse.StepAction.DEPART)
                .type(null)
                .stopName(stopName)
                .primaryText(stopName)
                .secondaryText("출발")
                .minutes(null)
                .colorHex(null)
                .line(null)
                .build();
    }

    /**
     * WALK step 생성
     */
    private TransitRoutesResponse.StepResponse createWalkStep(RootResult.DistanceStep step, ChipType chipType) {
        String colorHex = (chipType != null) ? chipType.defaultBg : ChipType.WALK.defaultBg;

        return TransitRoutesResponse.StepResponse.builder()
                .stepIndex(0)
                .action(TransitRoutesResponse.StepAction.WALK)
                .type("WALK")
                .stopName(null)
                .primaryText("도보 이동")
                .secondaryText("약 " + TimeFormatter.formatTime(step.time()))
                .minutes(step.time())
                .colorHex(colorHex)
                .line(null)
                .build();
    }

    /**
     * BOARD step 생성 (승차)
     */
    private TransitRoutesResponse.StepResponse createBoardStep(RootResult.DistanceStep step, ChipType chipType) {
        String stopType = getStopTypeSuffix(step.type());
        String secondaryText = step.lineInfo();

        // 버스 노선 축약
        if (step.type() == RootResult.TransportType.BUS && step.lineInfo() != null) {
            secondaryText = abbreviateBusNumbers(step.lineInfo());
        }

        // 색상 추출
        String colorHex = TransportColorResolver.extractBgColorHex(step, chipType);

        return TransitRoutesResponse.StepResponse.builder()
                .stepIndex(0)
                .action(TransitRoutesResponse.StepAction.BOARD)
                .type(step.type().name())
                .stopName(step.startName())
                .primaryText(step.startName() + stopType + " 승차")
                .secondaryText(secondaryText)
                .minutes(step.time())
                .colorHex(colorHex)
                .line(step.line())
                .build();
    }

    /**
     * ALIGHT step 생성 (하차)
     */
    private TransitRoutesResponse.StepResponse createAlightStep(RootResult.DistanceStep step, ChipType chipType) {
        String stopType = getStopTypeSuffix(step.type());

        // 색상 추출 (ALIGHT는 해당 교통수단의 색상 유지)
        String colorHex = TransportColorResolver.extractBgColorHex(step, chipType);

        return TransitRoutesResponse.StepResponse.builder()
                .stepIndex(0)
                .action(TransitRoutesResponse.StepAction.ALIGHT)
                .type(step.type().name())
                .stopName(step.endName())
                .primaryText(step.endName() + stopType + " 하차")
                .secondaryText(step.lineInfo())
                .minutes(null)
                .colorHex(colorHex)
                .line(step.line())
                .build();
    }

    /**
     * ARRIVE step 생성
     */
    private TransitRoutesResponse.StepResponse createArriveStep(String stopName) {
        return TransitRoutesResponse.StepResponse.builder()
                .stepIndex(0)
                .action(TransitRoutesResponse.StepAction.ARRIVE)
                .type(null)
                .stopName(stopName)
                .primaryText(stopName)
                .secondaryText("도착")
                .minutes(null)
                .colorHex(null)
                .line(null)
                .build();
    }

    /**
     * Step 인덱스 부여
     */
    private List<TransitRoutesResponse.StepResponse> assignStepIndexes(List<TransitRoutesResponse.StepResponse> steps) {
        List<TransitRoutesResponse.StepResponse> result = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            TransitRoutesResponse.StepResponse original = steps.get(i);
            result.add(TransitRoutesResponse.StepResponse.builder()
                    .stepIndex(i)
                    .action(original.action())
                    .type(original.type())
                    .stopName(original.stopName())
                    .primaryText(original.primaryText())
                    .secondaryText(original.secondaryText())
                    .minutes(original.minutes())
                    .colorHex(original.colorHex())
                    .line(original.line())
                    .build());
        }
        return result;
    }

    /**
     * 정류장/역 접미어 반환
     */
    private String getStopTypeSuffix(RootResult.TransportType type) {
        if (type == RootResult.TransportType.SUBWAY || type == RootResult.TransportType.TRAIN) {
            return "역";
        } else if (type == RootResult.TransportType.BUS) {
            return "정류장";
        }
        return "";
    }

    /**
     * 버스 번호 축약
     */
    private String abbreviateBusNumbers(String busNumbers) {
        if (busNumbers == null) return null;
        String[] numbers = busNumbers.split(",\\s*");
        if (numbers.length <= 3) {
            return busNumbers + "번";
        }
        String first3 = String.join(", ", numbers[0], numbers[1], numbers[2]);
        int remaining = numbers.length - 3;
        return first3 + "번 외 " + remaining + "개";
    }
}
