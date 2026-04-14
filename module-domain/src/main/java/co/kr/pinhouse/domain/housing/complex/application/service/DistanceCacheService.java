package co.kr.pinhouse.domain.housing.complex.application.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitInfoResponse;
import co.kr.pinhouse.domain.housing.complex.domain.transit.RootResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 거리/시간 정보 Redis 캐싱 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistanceCacheService {

	private static final String ROOT_RESULT_PREFIX = "rootresult:";
	private static final String TRANSIT_INFO_PREFIX = "transitinfo:";
	private static final long CACHE_TTL_HOURS = 24;
	private final RedisTemplate<String, Object> redisTemplate;

	// =================
	//  RootResult 캐싱 메서드
	// =================

	/**
	 * RootResult 캐시 키 생성
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 * @return rootresult:{complexId}:{pinPointId}
	 */
	private String generateRootResultKey(String complexId, String pinPointId) {
		return ROOT_RESULT_PREFIX + complexId + ":" + pinPointId;
	}

	private String generateTransitInfoKey(String complexId, String pinPointId) {
		return TRANSIT_INFO_PREFIX + complexId + ":" + pinPointId;
	}

	/**
	 * RootResult 캐시에 저장
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 * @param rootResult 경로 정보
	 */
	public void cacheRootResult(String complexId, String pinPointId, RootResult rootResult) {
		try {
			String key = generateRootResultKey(complexId, pinPointId);
			redisTemplate.opsForValue().set(key, rootResult, CACHE_TTL_HOURS, TimeUnit.HOURS);
			log.debug("Cached RootResult for complexId={}, pinPointId={}", complexId, pinPointId);
		} catch (Exception e) {
			log.error("Failed to cache RootResult: complexId={}, pinPointId={}", complexId, pinPointId, e);
		}
	}

	/**
	 * 캐시에서 RootResult 조회
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 * @return 캐시된 RootResult, 없으면 null
	 */
	public RootResult getRootResult(String complexId, String pinPointId) {
		try {
			String key = generateRootResultKey(complexId, pinPointId);
			Object cached = redisTemplate.opsForValue().get(key);

			if (cached instanceof RootResult) {
				log.debug("RootResult cache hit for complexId={}, pinPointId={}", complexId, pinPointId);
				return (RootResult)cached;
			}

			log.debug("RootResult cache miss for complexId={}, pinPointId={}", complexId, pinPointId);
			return null;
		} catch (Exception e) {
			log.error("Failed to get cached RootResult: complexId={}, pinPointId={}", complexId, pinPointId, e);
			return null;
		}
	}

	/**
	 * 상세조회용 TransitInfoResponse 캐시에 저장
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 * @param transitInfo 상세조회용 교통 응답
	 */
	public void cacheTransitInfo(String complexId, String pinPointId, TransitInfoResponse transitInfo) {
		try {
			String key = generateTransitInfoKey(complexId, pinPointId);
			redisTemplate.opsForValue().set(key, transitInfo, CACHE_TTL_HOURS, TimeUnit.HOURS);
			log.debug("Cached TransitInfo for complexId={}, pinPointId={}", complexId, pinPointId);
		} catch (Exception e) {
			log.error("Failed to cache TransitInfo: complexId={}, pinPointId={}", complexId, pinPointId, e);
		}
	}

	/**
	 * 상세조회용 TransitInfoResponse 조회
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 * @return 캐시된 TransitInfoResponse, 없으면 null
	 */
	public TransitInfoResponse getTransitInfo(String complexId, String pinPointId) {
		try {
			String key = generateTransitInfoKey(complexId, pinPointId);
			Object cached = redisTemplate.opsForValue().get(key);

			if (cached instanceof TransitInfoResponse) {
				log.debug("TransitInfo cache hit for complexId={}, pinPointId={}", complexId, pinPointId);
				return (TransitInfoResponse)cached;
			}

			log.debug("TransitInfo cache miss for complexId={}, pinPointId={}", complexId, pinPointId);
			return null;
		} catch (Exception e) {
			log.error("Failed to get cached TransitInfo: complexId={}, pinPointId={}", complexId, pinPointId, e);
			return null;
		}
	}

	/**
	 * 특정 RootResult 캐시 삭제
	 * @param complexId 단지 ID
	 * @param pinPointId 핀포인트 ID
	 */
	public void evictRootResult(String complexId, String pinPointId) {
		try {
			String key = generateRootResultKey(complexId, pinPointId);
			redisTemplate.delete(key);
			redisTemplate.delete(generateTransitInfoKey(complexId, pinPointId));
			log.debug("Evicted RootResult cache for complexId={}, pinPointId={}", complexId, pinPointId);
		} catch (Exception e) {
			log.error("Failed to evict RootResult cache: complexId={}, pinPointId={}", complexId, pinPointId, e);
		}
	}
}
