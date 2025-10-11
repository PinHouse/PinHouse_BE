package com.pinHouse.server.platform.housing.deposit.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * [ 사용자 변환에 따른 임대 옵션 응답] DTO
 * - 임대 옵션 정보를 응답할 때 사용합니다.
 * - 추후, 해당 부분은 백엔드의 기능이 아닌, 프런트를 통해 이뤄질 수 있습니다
 *
 * @param noticeId                공고 아이디
 * @param deposit           변환된 보증금
 * @param rent              변환된 월세
 */

@Schema(name = "[응답][임대 옵션] 임대 옵션 정보 응답", description = "임대 옵션 정보를 응답할 때 사용되는 DTO입니다.")
@Builder
public record NoticeLeaseOptionResponse(
        @Schema(description = "공고 아이디", example = "N20241011-001")
        String noticeId,

        @Schema(description = "주택 유형", example = "아파트")
        String housingType,

        @Schema(description = "변환된 보증금", example = "50000000")
        long deposit,

        @Schema(description = "변환된 월세", example = "500000")
        long rent
) {

    /// 정적 팩토리 메서드
    public static NoticeLeaseOptionResponse from(String noticeId, String housingType, long deposit, long rent) {

        return NoticeLeaseOptionResponse.builder()
                .noticeId(noticeId)
                .housingType(housingType)
                .deposit(deposit)
                .rent(rent)
                .build();
    }

}
