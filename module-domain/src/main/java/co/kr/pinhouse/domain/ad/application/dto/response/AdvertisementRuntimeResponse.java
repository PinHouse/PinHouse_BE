package co.kr.pinhouse.domain.ad.application.dto.response;

import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementLinkType;
import lombok.Builder;

@Builder
public record AdvertisementRuntimeResponse(
	Long advertisementId,
	String title,
	String imageUrl,
	AdvertisementLinkType linkType,
	String linkValue
) {

	public static AdvertisementRuntimeResponse from(Advertisement advertisement) {
		return AdvertisementRuntimeResponse.builder()
			.advertisementId(advertisement.getId())
			.title(advertisement.getTitle())
			.imageUrl(advertisement.getImageUrl())
			.linkType(advertisement.getLinkType())
			.linkValue(advertisement.getLinkValue())
			.build();
	}
}
