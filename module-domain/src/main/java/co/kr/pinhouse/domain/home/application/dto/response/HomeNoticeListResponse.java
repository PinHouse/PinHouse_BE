package co.kr.pinhouse.domain.home.application.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][홈] 홈 화면 공고 목록 조회 응답", description = "홈 화면에서 마감임박 공고 목록을 조회하기 위한 DTO입니다. SliceResponse와 유사한 구조에 region 필드를 추가했습니다.")
@Builder
public record HomeNoticeListResponse(

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "공통 지역", example = "성남시")
	String region,

	@Schema(description = "목록 설명/출처", example = "진단 기반 추천")
	String title,

	@Schema(description = "공고 목록")
	List<HomeNoticeResponse> content,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	boolean hasNext,

	@Schema(description = "전체 공고 개수", example = "100")
	long totalElements,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "진단 기반 추천 응답에서 진단 기록 존재 여부를 제공합니다.", example = "true", nullable = true)
	Boolean hasDiagnosis,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "목록이 비어 있는 이유. 진단 기반 추천 응답에서만 사용됩니다.", example = "NO_DIAGNOSIS", nullable = true)
	HomeNoticeEmptyReason emptyReason,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "목록이 비어 있는 사유를 설명하는 메시지. 진단 기반 추천 응답에서만 사용됩니다.", example = "진단 기록이 없어 추천 공고를 제공할 수 없습니다.", nullable = true)
	String emptyMessage

) {
}
