package com.pinHouse.server.platform.domain.facility;

import com.pinHouse.server.platform.domain.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Library implements Facility{

    private String id;
    private Integer code;
    private String name;
    private String address;
    private Location location;
    private String area;
    private String city;
    private Integer number;
    private String openTime;
    private String closedDay;
}
