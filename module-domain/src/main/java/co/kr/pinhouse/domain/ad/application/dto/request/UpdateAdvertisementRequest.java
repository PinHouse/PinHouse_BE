package co.kr.pinhouse.domain.ad.application.dto.request;

import java.time.LocalDateTime;

import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementLinkType;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;

public record UpdateAdvertisementRequest(
	String title,
	AdvertisementPlacement placement,
	String imageUrl,
	AdvertisementLinkType linkType,
	String linkValue,
	LocalDateTime startAt,
	LocalDateTime endAt,
	Integer priority
) {
}
