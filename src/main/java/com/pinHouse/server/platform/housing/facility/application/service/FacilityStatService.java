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

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityStatService {

    private final MongoTemplate mongoTemplate;
    private final FacilityStatDocumentRepository countsRepo;

    // === 상수 ===
    private static final double RADIUS_KM = 3;
    private static final double RADIUS_M  = RADIUS_KM * 1000.0;
    private static final Duration TTL = Duration.ofDays(7);

    /// 특정 인프라가 존재하는 내용 가져오기
    public List<FacilityStatDocument> findByAllTypesOver(Collection<FacilityType> types, int min) {
        List<Criteria> ands = types.stream()
                .map(t -> Criteria.where("counts." + t.name()).gte(min))
                .toList();

        Query q = new Query(new Criteria().andOperator(ands.toArray(new Criteria[0])));
        return mongoTemplate.find(q, FacilityStatDocument.class);
    }


    /// 계산하기
    public Map<FacilityType, Integer> getCountsOrRecompute(String complexId, double lng, double lat) {
        FacilityStatDocument existing = countsRepo.findById(complexId)
                .orElse(null);

        if (existing != null && !isExpired(existing.getUpdatedAt())) {
            return existing.getCounts();
        }

        Map<FacilityType, Integer> fresh = aggregateCounts(lng, lat, RADIUS_M);
        upsertCounts(complexId, fresh);
        return fresh;
    }

    /** $geoNear → $group 로 타입별 개수 */
    private Map<FacilityType, Integer> aggregateCounts(double lng, double lat, double radiusMeters) {
        GeoJsonPoint near = new GeoJsonPoint(lng, lat);

        GeoNearOperation geoNear = Aggregation.geoNear(
                NearQuery.near(near)
                        .maxDistance(new org.springframework.data.geo.Distance(RADIUS_KM, Metrics.KILOMETERS))
                        .spherical(true),
                "dist"
        );

        GroupOperation groupByType = Aggregation.group("type").count().as("cnt");

        ProjectionOperation project = Aggregation.project()
                .and("_id").as("type")
                .and("cnt").as("cnt")
                .andExclude("_id");

        Aggregation agg = Aggregation.newAggregation(geoNear, groupByType, project);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "facilities", org.bson.Document.class);

        log.info("시설 통계 집계 결과: {}", results.getMappedResults());

        Map<FacilityType, Integer> map = new EnumMap<>(FacilityType.class);
        for (org.bson.Document d : results) {
            String typeStr = d.getString("type");
            int cnt = d.getInteger("cnt", 0);
            try {
                map.put(FacilityType.valueOf(typeStr), cnt);
            } catch (Exception ignore) { }
        }
        for (FacilityType t : FacilityType.values()) {
            map.putIfAbsent(t, 0);
        }
        return map;
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
