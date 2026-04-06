package co.kr.pinhouse.domain.housing.facility;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.housing.facility.application.dto.NoticeFacilityListResponse;
import co.kr.pinhouse.domain.housing.facility.application.usecase.FacilityUseCase;
import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityType;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/complexes/infra")
@RequiredArgsConstructor
public class FacilityApi implements FacilityApiSpec {

	/// 서비스 의존성
	private final FacilityUseCase service;

	/// 주변 인프라 조회
	@GetMapping("/{complexId}")
	public ApiResponse<NoticeFacilityListResponse> showNotice(
		@PathVariable String complexId) {

		/// 서비스 계층
		var response = service.getNearFacilities(complexId);

		/// 리턴
		return ApiResponse.ok(response);
	}

	/// 원하는 인프라를 바탕으로 많이 존재하는 지역을 설정
	@GetMapping()
	public ApiResponse<List<ComplexDocument>> showNoticeList(
		@RequestParam List<FacilityType> facilityTypes
	) {

		/// 서비스 계층
		var response = service.getComplexes(facilityTypes);

		/// 리턴
		return ApiResponse.ok(response);
	}

}
