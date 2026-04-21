package co.kr.pinhouse.domain.ad.application.dto.request;

import java.time.LocalDateTime;

import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementLinkType;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementPlacement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAdvertisementRequest(
	@NotBlank String title,
	@NotNull AdvertisementPlacement placement,
	@NotBlank String imageUrl,
	@NotNull AdvertisementLinkType linkType,
	String linkValue,
	LocalDateTime startAt,
	LocalDateTime endAt,
	Integer priority
) {
}
