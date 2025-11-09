package com.pinHouse.server.platform.housing.complex.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DepositMinMaxResponse(
        @Schema(description = "보증금 합계", example = "50000000")
        long total,

        @Schema(description = "계약금", example = "50000000")
        long contract,

        @Schema(description = "잔금", example = "50000000")
        long balance,

        @Schema(description = "월 임대료", example = "500000")
        long monthPay
) {
}
