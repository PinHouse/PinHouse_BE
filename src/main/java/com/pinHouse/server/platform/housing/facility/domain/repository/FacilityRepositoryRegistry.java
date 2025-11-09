package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.core.exception.code.FacilityErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.facility.domain.entity.Facility;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FacilityRepositoryRegistry {

    private final List<GeoFacilityRepository<? extends Facility>> repositories;
    private Map<FacilityType, GeoFacilityRepository<? extends Facility>> map;

    @PostConstruct
    void init() {
        this.map = new EnumMap<>(FacilityType.class);
        for (GeoFacilityRepository<? extends Facility> repo : repositories) {
            map.put(repo.supportsType(), repo);
        }
    }

    public GeoFacilityRepository<? extends Facility> get(FacilityType type) {
        GeoFacilityRepository<? extends Facility> repo = map.get(type);

        /// 타입
        if (repo == null) {
            throw new CustomException(FacilityErrorCode.BAD_REQUEST_FACILITY);
        }

        return repo;
    }

}
