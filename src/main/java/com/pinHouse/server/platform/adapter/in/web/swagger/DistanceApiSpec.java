package com.pinHouse.server.platform.adapter.in.web.swagger;

import io.swagger.v3.oas.annotations.Operation;
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
            @RequestParam double startY,
            @RequestParam double startX,
            @RequestParam double endY,
            @RequestParam double endX
    ) throws UnsupportedEncodingException;

}
