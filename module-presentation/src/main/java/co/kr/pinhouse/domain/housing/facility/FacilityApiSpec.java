package co.kr.pinhouse.domain.housing.facility;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.facility.application.dto.NoticeFacilityListResponse;
import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "임대주택 주변 인프라 API", description = "인프라를 바탕으로 조회하는 API 입니다.")
public interface FacilityApiSpec {

	/// 공고 주변 인프라 조회
	@Operation(
		summary = "공고 주변 인프라 조회 API",
		description = "공고 주변 3KM내 존재하는 인프라를 확인하는 API입니다."
	)
	ApiResponse<NoticeFacilityListResponse> showNotice(
		@Parameter(example = "19413#1")
		@PathVariable String complexId);

	/// 인프라 많은 공고 조회
	@Operation(
		summary = "인프라에 따른 공고 조회 API",
		description = "해당 인프라가 2개 이상 존재하는 공고를 조회하는 API입니다."
	)
	ApiResponse<List<ComplexDocument>> showNoticeList(
		@RequestParam List<FacilityType> facilityTypes
	);
}
