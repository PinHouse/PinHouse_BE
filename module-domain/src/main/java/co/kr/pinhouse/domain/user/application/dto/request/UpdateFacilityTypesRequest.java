package co.kr.pinhouse.domain.user.application.dto.request;

import java.util.List;

import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관심 시설 타입 수정 요청")
public record UpdateFacilityTypesRequest(

	@Schema(description = "관심 시설 타입 목록", example = "[\"문화센터\", \"병원-약국\", \"실내 액티비티\"]")
	@NotNull(message = "관심 시설 타입 목록은 필수입니다")
	List<FacilityType> facilityTypes
) {
}
