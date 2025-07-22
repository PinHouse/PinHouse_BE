package com.myhome.server.platform.adapter.in.web;

import com.myhome.server.platform.application.out.distance.DistancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/distance")
@RequiredArgsConstructor
public class DistanceApi {

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
