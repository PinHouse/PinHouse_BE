package com.pinHouse.server.platform.distance.presentation;

import com.pinHouse.server.platform.distance.presentation.swaager.DistanceApiSpec;
import com.pinHouse.server.platform.distance.application.usecase.DistancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/notices/distance")
@RequiredArgsConstructor
public class DistanceApi implements DistanceApiSpec {

    private final DistancePort distancePort;

    @GetMapping()
    public String getDistance(
            @RequestParam double startY,
            @RequestParam double startX,
            @RequestParam double endY,
            @RequestParam double endX
    ) throws UnsupportedEncodingException {

        String path = distancePort.findPath(startY, startX, endY, endX);

        return path;
    }

}
