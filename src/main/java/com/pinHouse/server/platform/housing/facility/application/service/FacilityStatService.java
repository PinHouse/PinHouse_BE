package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.repository.FacilityStatDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class FacilityStatService {

    private final MongoTemplate mongoTemplate;
    private final FacilityStatDocumentRepository countsRepo;

    /// 상수
    private final double radiusKm = 1;
    private final double RADIUS_IN_RAD = radiusKm / 6371.0;


    private static final Duration TTL = Duration.ofDays(7);

    // =================
    //  퍼블릭 로직
    // =================

    /// 가져오기
    public Map<FacilityType,Integer> getCountsOrRecompute(String complexId, double lng, double lat) {

        /// 기존에 존재하는 값 가져오기
        FacilityStatDocument existing = countsRepo.findById(complexId).orElse(null);

        /// 없거나 만료되었으면 새롭게 조회
        if (existing != null && !isExpired(existing.getUpdatedAt())) {
            return existing.getCounts();
        }

        /// 거리 기반 조횐
        Map<FacilityType,Integer> fresh = aggregateCounts(lng, lat, RADIUS_IN_RAD);
        upsertCounts(complexId, fresh);

        /// 리턴
        return fresh;
    }



    // =================
    //  내부 로직
    // =================

    /** $geoNear → $group 로 타입별 개수 */
    private Map<FacilityType,Integer> aggregateCounts(double lng, double lat, double radiusM) {
        GeoJsonPoint near = new GeoJsonPoint(lng, lat);

        /// 거리 기반 조회
        GeoNearOperation geoNear = Aggregation.geoNear(
                NearQuery.near(near).maxDistance(radiusM).spherical(true),
                "dist" // distanceField
        );

        // 2) $group by type
        GroupOperation groupByType = group("type").count().as("cnt");

        // 3) $project
        ProjectionOperation project = project()
                .and("_id").as("type")
                .and("cnt").as("cnt")
                .andExclude("_id");

        Aggregation agg = newAggregation(geoNear, groupByType, project);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "facilities", Document.class);

        Map<FacilityType,Integer> map = new EnumMap<>(FacilityType.class);
        for (Document d : results) {
            String typeStr = d.getString("type");
            int cnt = d.getInteger("cnt", 0);
            // FacilityType 파싱 실패 대비
            try {
                map.put(FacilityType.valueOf(typeStr), cnt);
            } catch (Exception ignore) {
                /* 알 수 없는 타입은 스킵 */ }
        }

        // 없는 타입은 0으로 채우기(응답 일관성)
        for (FacilityType t : FacilityType.values()) {
            map.putIfAbsent(t, 0);
        }
        return map;
    }

    /// 개수 Upsert
    private void upsertCounts(String complexId, Map<FacilityType,Integer> counts) {
        FacilityStatDocument doc = FacilityStatDocument.builder()
                .id(complexId)
                .radiusKm(RADIUS_IN_RAD)
                .counts(counts)
                .updatedAt(Instant.now())
                .build();

        countsRepo.save(doc);
    }

    /// 만료되었는지 체크
    private boolean isExpired(Instant t) {
        return t == null || t.isBefore(Instant.now().minus(TTL));
    }
}
