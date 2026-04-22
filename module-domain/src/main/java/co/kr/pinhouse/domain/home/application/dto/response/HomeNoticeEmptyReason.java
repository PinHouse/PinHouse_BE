package co.kr.pinhouse.domain.home.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 공고 목록이 비어 있는 이유")
public enum HomeNoticeEmptyReason {

	@Schema(description = "진단 기록이 없어 추천 공고를 생성할 수 없음")
	NO_DIAGNOSIS,

	@Schema(description = "진단 결과상 추천 가능한 임대주택 유형이 없음")
	NO_ELIGIBLE_RENTAL_TYPES,

	@Schema(description = "진단 결과를 공고 필터 조건으로 매핑할 수 없음")
	NO_MAPPED_SUPPLY_TYPES,

	@Schema(description = "진단 결과에 맞는 공고가 현재 없음")
	NO_MATCHING_NOTICES
}
