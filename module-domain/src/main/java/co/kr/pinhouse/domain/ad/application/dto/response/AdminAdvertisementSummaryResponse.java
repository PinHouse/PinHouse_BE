package co.kr.pinhouse.domain.ad.application.dto.response;

import java.time.LocalDateTime;

import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import lombok.Builder;

@Builder
public record AdminAdvertisementSummaryResponse(
	Long advertisementId,
	String title,
	AdvertisementStatus status,
	AdvertisementPlacement placement,
	LocalDateTime startAt,
	LocalDateTime endAt,
	int priority,
	LocalDateTime createdAt
) {

	public static AdminAdvertisementSummaryResponse from(Advertisement advertisement) {
		return AdminAdvertisementSummaryResponse.builder()
			.advertisementId(advertisement.getId())
			.title(advertisement.getTitle())
			.status(advertisement.getStatus())
			.placement(advertisement.getPlacement())
			.startAt(advertisement.getStartAt())
			.endAt(advertisement.getEndAt())
			.priority(advertisement.getPriority())
			.createdAt(advertisement.getCreatedAt())
			.build();
	}
}
