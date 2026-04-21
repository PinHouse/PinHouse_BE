package co.kr.pinhouse.domain.ad.application.dto.response;

import java.time.LocalDateTime;

import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementLinkType;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import lombok.Builder;

@Builder
public record AdminAdvertisementResponse(
	Long advertisementId,
	String title,
	AdvertisementStatus status,
	AdvertisementPlacement placement,
	String imageUrl,
	AdvertisementLinkType linkType,
	String linkValue,
	LocalDateTime startAt,
	LocalDateTime endAt,
	int priority,
	long impressionCount,
	long clickCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static AdminAdvertisementResponse of(
		Advertisement advertisement,
		long impressionCount,
		long clickCount
	) {
		return AdminAdvertisementResponse.builder()
			.advertisementId(advertisement.getId())
			.title(advertisement.getTitle())
			.status(advertisement.getStatus())
			.placement(advertisement.getPlacement())
			.imageUrl(advertisement.getImageUrl())
			.linkType(advertisement.getLinkType())
			.linkValue(advertisement.getLinkValue())
			.startAt(advertisement.getStartAt())
			.endAt(advertisement.getEndAt())
			.priority(advertisement.getPriority())
			.impressionCount(impressionCount)
			.clickCount(clickCount)
			.createdAt(advertisement.getCreatedAt())
			.updatedAt(advertisement.getUpdatedAt())
			.build();
	}
}
