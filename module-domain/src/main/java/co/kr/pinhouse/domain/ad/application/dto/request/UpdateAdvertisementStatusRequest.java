package co.kr.pinhouse.domain.ad.application.dto.request;

import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAdvertisementStatusRequest(
	@NotNull AdvertisementStatus status
) {
}
