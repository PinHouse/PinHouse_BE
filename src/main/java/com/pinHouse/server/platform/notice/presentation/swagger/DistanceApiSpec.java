package com.pinHouse.server.platform.notice.presentation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Tag(name = "거리 시뮬레이터 API", description = "원하는 주소를 바탕으로 대중교통 시뮬레이터를 하는 API입니다.")
public interface DistanceApiSpec {

    @Operation(
            summary = "거리 시뮬레이터 API",
            description = "출발 좌표와 도착 좌표를 통해 계산을 진행합니다.")
    @GetMapping()
    String getDistance(
            @Parameter(example = "37.2479117750768", description = "출발지 공고의 위도")
            @RequestParam double startY,

            @Parameter(example = "126.866087591443", description = "출발지 공고의 경도")
            @RequestParam double startX,

            @Parameter(example = "37.566535", description = "도착지 좌표 위도(예시는 서울시)")
            @RequestParam double endY,

            @Parameter(example = "126.9779692", description = "도착지 좌표 경도(예시는 서울시)")
            @RequestParam double endX
    ) throws UnsupportedEncodingException;

}
