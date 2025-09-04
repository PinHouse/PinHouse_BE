package com.pinHouse.server.platform.facility.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private String type;
    private List<Double> coordinates;

    @JsonIgnore
    public Double getLongitude() {
        return coordinates.get(0);
    }

    @JsonIgnore
    public Double getLatitude() {
        return coordinates.get(1);
    }
}
