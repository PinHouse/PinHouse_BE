package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

@RequiredArgsConstructor
public class FacilityStatDocumentRepositoryImpl implements FacilityStatDocumentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<FacilityStatDocument> findByAllTypesOver(Collection<FacilityType> types, int min) {
        if (types == null || types.isEmpty()) {
            return List.of();
        }

        // 컬렉션 존재 여부 확인
        if (!mongoTemplate.collectionExists(FacilityStatDocument.class)) {
            // 컬렉션이 아예 없으면 빈 리스트 반환
            return List.of();
        }

        List<Criteria> ands = types.stream()
                .map(t -> {
                    if (t == FacilityType.CULTURE_CENTER) {
                        List<Criteria> ors = new ArrayList<>();
                        ors.add(Criteria.where("counts." + FacilityType.CULTURE_CENTER.name()).gte(min));
                        FacilityType.cultureCenterMembers().forEach(member ->
                                ors.add(Criteria.where("counts." + member.name()).gte(min))
                        );
                        return new Criteria().orOperator(ors.toArray(new Criteria[0]));
                    }
                    return Criteria.where("counts." + t.name()).gte(min);
                })
                .toList();

        Query q = new Query(new Criteria().andOperator(ands.toArray(new Criteria[0])));
        return mongoTemplate.find(q, FacilityStatDocument.class);
    }

    /** $geoNear → $group 로 타입별 개수 */
    @Override
    public Map<FacilityType, Integer> aggregateCounts(double lng, double lat, double radiusMeters) {
        // facilities 컬렉션 없을 경우 바로 빈 맵 반환
        if (!mongoTemplate.collectionExists("facilities")) {
            return initEmptyMap();
        }

        GeoJsonPoint near = new GeoJsonPoint(lng, lat);
        double radiusKm = radiusMeters / 1000.0;

        GeoNearOperation geoNear = Aggregation.geoNear(
                NearQuery.near(near)
                        .maxDistance(new org.springframework.data.geo.Distance(radiusKm, Metrics.KILOMETERS))
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
                mongoTemplate.aggregate(agg, "facilities", Document.class);

        // 결과가 비었으면 -> 빈 맵 생성
        if (!results.iterator().hasNext()) {
            return initEmptyMap();
        }

        Map<FacilityType, Integer> map = new EnumMap<>(FacilityType.class);
        for (Document d : results) {
            String typeStr = d.getString("type");
            int cnt = d.getInteger("cnt", 0);
            try {
                map.put(FacilityType.valueOf(typeStr), cnt);
            } catch (Exception ignore) { }
        }

        // 누락된 타입 0으로 채우기
        for (FacilityType t : FacilityType.values()) {
            map.putIfAbsent(t, 0);
        }
        map.put(FacilityType.CULTURE_CENTER, computeCultureCenterCount(map));
        return map;
    }

    private Map<FacilityType, Integer> initEmptyMap() {
        Map<FacilityType, Integer> empty = new EnumMap<>(FacilityType.class);
        for (FacilityType t : FacilityType.values()) {
            empty.put(t, 0);
        }
        empty.put(FacilityType.CULTURE_CENTER, 0);
        return empty;
    }

    private int computeCultureCenterCount(Map<FacilityType, Integer> map) {
        return FacilityType.cultureCenterMembers().stream()
                .mapToInt(t -> map.getOrDefault(t, 0))
                .sum();
    }

}
