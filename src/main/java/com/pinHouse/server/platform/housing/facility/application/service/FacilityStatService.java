package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.repository.FacilityStatDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
            Map<FacilityType, Integer> fresh = withDerivedCounts(countsRepo.aggregateCounts(lng, lat, RADIUS_M));
            upsertCounts(complexId, fresh);
            return fresh;
        }

        // TTL 지나면 재계산
        if (isExpired(existing.getUpdatedAt())) {
            Map<FacilityType, Integer> fresh = withDerivedCounts(countsRepo.aggregateCounts(lng, lat, RADIUS_M));
            upsertCounts(complexId, fresh);
            return fresh;
        }

        // 유효한 캐시면 그대로 사용
        return withDerivedCounts(existing.getCounts());
    }


    private void upsertCounts(String complexId, Map<FacilityType, Integer> counts) {
        FacilityStatDocument doc = FacilityStatDocument.builder()
                .id(complexId)
                .radiusKm(RADIUS_KM)
                .counts(withDerivedCounts(counts))
                .updatedAt(Instant.now())
                .build();
        countsRepo.save(doc);
    }

    private boolean isExpired(Instant t) {
        return t == null || t.isBefore(Instant.now().minus(TTL));
    }

    private Map<FacilityType, Integer> withDerivedCounts(Map<FacilityType, Integer> counts) {
        if (counts == null) {
            return Map.of();
        }

        Map<FacilityType, Integer> enriched = new EnumMap<>(FacilityType.class);
        for (FacilityType type : FacilityType.values()) {
            enriched.put(type, counts.getOrDefault(type, 0));
        }
        enriched.put(FacilityType.CULTURE_CENTER, computeCultureCenterCount(enriched));
        return enriched;
    }

    private int computeCultureCenterCount(Map<FacilityType, Integer> map) {
        return FacilityType.cultureCenterMembers().stream()
                .mapToInt(t -> map.getOrDefault(t, 0))
                .sum();
    }
}
