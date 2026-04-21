package co.kr.pinhouse.domain.ad.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEvent;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEventType;

public interface AdvertisementEventRepository extends JpaRepository<AdvertisementEvent, Long> {

	long countByAdvertisement_IdAndEventType(Long advertisementId, AdvertisementEventType eventType);
}
