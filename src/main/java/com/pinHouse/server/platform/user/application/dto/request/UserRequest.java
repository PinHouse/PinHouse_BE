package com.pinHouse.server.platform.user.application.dto.request;

import com.pinHouse.server.platform.housing.facility.application.dto.request.FacilityType;
import lombok.Data;
import java.util.List;

@Data
public class UserRequest {

    /// 목록 리스트
    private List<FacilityType> facilityTypes;

}
