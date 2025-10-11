package com.pinHouse.server.platform.housing.distance.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.distance.application.dto.DistanceResponse;
import com.pinHouse.server.platform.housing.distance.presentation.swaager.DistanceApiSpec;
import com.pinHouse.server.platform.housing.distance.application.usecase.DistanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notices/distance")
@RequiredArgsConstructor
public class DistanceApi implements DistanceApiSpec {

    private final DistanceUseCase distanceUseCase;

    @GetMapping
    public ApiResponse<List<DistanceResponse>> getDistance(
            @RequestParam double startY,
            @RequestParam double startX,
            @RequestParam double endY,
            @RequestParam double endX
    ) throws UnsupportedEncodingException {

        /// 리턴
        return ApiResponse.ok(distanceUseCase.findPath(startY, startX, endY, endX));
    }

}
