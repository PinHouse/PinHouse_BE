package com.pinHouse.server.core.cache;

import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 거리/시간 정보 Redis 캐싱 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistanceCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "distance:";
    private static final long CACHE_TTL_HOURS = 24;

    /**
     * 캐시 키 생성
     * @param complexId 단지 ID
     * @param pinPointId 핀포인트 ID
     * @return distance:{complexId}:{pinPointId}
     */
    private String generateKey(String complexId, String pinPointId) {
        return CACHE_PREFIX + complexId + ":" + pinPointId;
    }

    /**
     * 거리 정보 캐시에 저장
     * @param complexId 단지 ID
     * @param pinPointId 핀포인트 ID
     * @param distance 거리 정보
     */
    public void cacheDistance(String complexId, String pinPointId, DistanceResponse distance) {
        try {
            String key = generateKey(complexId, pinPointId);
            redisTemplate.opsForValue().set(key, distance, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("Cached distance for complexId={}, pinPointId={}", complexId, pinPointId);
        } catch (Exception e) {
            log.error("Failed to cache distance: complexId={}, pinPointId={}", complexId, pinPointId, e);
        }
    }

    /**
     * 캐시에서 거리 정보 조회
     * @param complexId 단지 ID
     * @param pinPointId 핀포인트 ID
     * @return 캐시된 거리 정보, 없으면 null
     */
    public DistanceResponse getDistance(String complexId, String pinPointId) {
        try {
            String key = generateKey(complexId, pinPointId);
            Object cached = redisTemplate.opsForValue().get(key);

            if (cached instanceof DistanceResponse) {
                log.debug("Cache hit for complexId={}, pinPointId={}", complexId, pinPointId);
                return (DistanceResponse) cached;
            }

            log.debug("Cache miss for complexId={}, pinPointId={}", complexId, pinPointId);
            return null;
        } catch (Exception e) {
            log.error("Failed to get cached distance: complexId={}, pinPointId={}", complexId, pinPointId, e);
            return null;
        }
    }

    /**
     * 특정 캐시 삭제
     * @param complexId 단지 ID
     * @param pinPointId 핀포인트 ID
     */
    public void evictDistance(String complexId, String pinPointId) {
        try {
            String key = generateKey(complexId, pinPointId);
            redisTemplate.delete(key);
            log.debug("Evicted distance cache for complexId={}, pinPointId={}", complexId, pinPointId);
        } catch (Exception e) {
            log.error("Failed to evict distance cache: complexId={}, pinPointId={}", complexId, pinPointId, e);
        }
    }
}
