package co.kr.pinhouse.domain.ad.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

	Page<Advertisement> findAllByOrderByCreatedAtDesc(Pageable pageable);

	List<Advertisement> findByPlacementAndStatusOrderByPriorityDescIdDesc(
		AdvertisementPlacement placement,
		AdvertisementStatus status
	);
}
