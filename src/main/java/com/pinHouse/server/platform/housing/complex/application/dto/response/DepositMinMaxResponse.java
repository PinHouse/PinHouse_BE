package com.pinHouse.server.platform.housing.complex.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DepositMinMaxResponse(
        @Schema(description = "보증금 합계 (만원 단위)", example = "5000")
        long total,

        @Schema(description = "계약금 (만원 단위)", example = "5000")
        long contract,

        @Schema(description = "잔금 (만원 단위)", example = "5000")
        long balance,

        @Schema(description = "월 임대료 (원)", example = "500000")
        long monthPay
) {
    /**
     * 원본 값(원 단위)을 받아서 보증금은 만원 단위로, 월세는 원 단위로 변환
     */
    public static DepositMinMaxResponse fromWon(long totalWon, long contractWon, long balanceWon, long monthPayWon) {
        return new DepositMinMaxResponse(
                totalWon / 10000,
                contractWon / 10000,
                balanceWon / 10000,
                monthPayWon  // 월세는 원 단위 그대로
        );
    }
}
