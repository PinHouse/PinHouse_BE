package com.pinHouse.server.platform.user.application.dto;

import com.pinHouse.server.platform.user.domain.entity.WithdrawReason;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 회원 탈퇴 요청 DTO
 */
@Schema(name = "[요청][유저] 회원 탈퇴", description = "회원 탈퇴 시 사유를 0개 이상 복수 선택 가능")
public record WithdrawRequest(
        @Schema(
                description = "탈퇴 사유 목록 (0개 이상 복수 선택 가능)",
                example = "[\"추천 결과가 내 조건과 잘 맞지 않아요\", \"원하는 공고/단지가 부족해요\"]"
        )
        List<WithdrawReason> reasons
) {
}
