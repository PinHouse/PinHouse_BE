package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.repository.FacilityStatDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacilityStatService {

    private final FacilityStatDocumentRepository countsRepo;

    // === 상수 ===
    private static final double RADIUS_KM = 3;
    private static final double RADIUS_M  = RADIUS_KM * 1000.0;
    private static final Duration TTL = Duration.ofDays(7);

    /// 특정 인프라가 존재하는 내용 가져오기
    public List<FacilityStatDocument> findByAllTypesOver(Collection<FacilityType> types, int min) {
        return countsRepo.findByAllTypesOver(types, min);
    }

    /// 계산하기 + 캐시(TTL)
    public Map<FacilityType, Integer> getCountsOrRecompute(String complexId, double lng, double lat) {
        FacilityStatDocument existing = countsRepo.findById(complexId).orElse(null);

        // 문서가 아예 없을 경우 → 새로 계산
        if (existing == null) {
            Map<FacilityType, Integer> fresh = countsRepo.aggregateCounts(lng, lat, RADIUS_M);
            upsertCounts(complexId, fresh);
            return fresh;
        }

        // TTL 지나면 재계산
        if (isExpired(existing.getUpdatedAt())) {
            Map<FacilityType, Integer> fresh = countsRepo.aggregateCounts(lng, lat, RADIUS_M);
            upsertCounts(complexId, fresh);
            return fresh;
        }

        // 유효한 캐시면 그대로 사용
        return existing.getCounts();
    }


    private void upsertCounts(String complexId, Map<FacilityType, Integer> counts) {
        FacilityStatDocument doc = FacilityStatDocument.builder()
                .id(complexId)
                .radiusKm(RADIUS_KM)
                .counts(counts)
                .updatedAt(Instant.now())
                .build();
        countsRepo.save(doc);
    }

    private boolean isExpired(Instant t) {
        return t == null || t.isBefore(Instant.now().minus(TTL));
    }
}
