package co.kr.pinhouse.domain.ad.application.dto.request;

import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEventType;
import jakarta.validation.constraints.NotNull;

public record RecordAdvertisementEventRequest(
	@NotNull Long advertisementId,
	@NotNull AdvertisementEventType eventType
) {
}
