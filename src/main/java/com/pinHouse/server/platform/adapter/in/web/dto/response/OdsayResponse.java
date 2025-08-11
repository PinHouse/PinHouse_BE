package com.pinHouse.server.platform.adapter.in.web.dto.response;


import java.util.List;

public record OdsayResponse(
        Result result
) {
    public record Result(
            int searchType,
            int outTrafficCheck,
            int busCount,
            int subwayCount,
            int subwayBusCount,
            int pointDistance,
            int startRadius,
            int endRadius,
            List<Path> path
    ) {}

    public record Path(
            int pathType,
            Info info,
            List<SubPath> subPath
    ) {}

    public record Info(
            double trafficDistance,
            int totalWalk,
            int totalTime,
            int payment,
            int busTransitCount,
            int subwayTransitCount,
            String mapObj,
            String firstStartStation,
            String lastEndStation,
            int totalStationCount,
            int busStationCount,
            int subwayStationCount,
            double totalDistance,
            int totalWalkTime,
            int checkIntervalTime,
            String checkIntervalTimeOverYn,
            int totalIntervalTime
    ) {}

    public record SubPath(
            int trafficType,
            int distance,
            int sectionTime,
            Integer stationCount,
            List<Lane> lane,
            Integer intervalTime,
            String startName,
            Double startX,
            Double startY,
            String endName,
            Double endX,
            Double endY,
            String way,
            Integer wayCode,
            String door,
            Integer startID,
            Integer endID,
            String startExitNo,
            Double startExitX,
            Double startExitY,
            String endExitNo,
            Double endExitX,
            Double endExitY,
            PassStopList passStopList
    ) {}

    public record Lane(
            String name,      // subway
            Integer subwayCode,
            Integer subwayCityCode,
            String busNo,     // bus
            Integer type,
            Integer busID,
            String busLocalBlID,
            Integer busCityCode,
            Integer busProviderCode
    ) {}

    public record PassStopList(
            List<Station> stations
    ) {}

    public record Station(
            int index,
            int stationID,
            String stationName,
            String x,
            String y,
            Integer stationCityCode,
            Integer stationProviderCode,
            String localStationID,
            String arsID,
            String isNonStop
    ) {}
}

