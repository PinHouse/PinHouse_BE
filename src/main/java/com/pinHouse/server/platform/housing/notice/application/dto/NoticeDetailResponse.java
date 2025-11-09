package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record NoticeDetailResponse(
        @Schema(description = "공고ID", example = "101")
        String id,

        @Schema(description = "임대유형", example = "영구임대")
        String type,

        @Schema(description = "주택유형", example = "아파트")
        String housingType,

        @Schema(description = "공급주체", example = "LH")
        String supplier,

        @Schema(description = "공고명", example = "2025 청년 행복주택 공고")
        String name,

        @Schema(description = "모집일정", example = "2025년 10월 ~ 11월")
        String period,

        @Schema(description = "전체 임대주택 개수", example = "6")
        long totalCount,

        List<ComplexDetailResponse> complexes
) {

    /// 정적 팩토리 메서드
    public static NoticeDetailResponse from(NoticeDocument notice, List<ComplexDocument> complexes, Map<String, NoticeFacilityListResponse> facilityListResponseMap) {

        /// 공고에 해당하는 임대주택 목록
        var complexesResponse = ComplexDetailResponse.from(complexes,facilityListResponseMap);

        String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());

        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .name(notice.getTitle())
                .supplier(notice.getAgency())
                .period(period)
                .type(notice.getSupplyType())
                .totalCount(complexesResponse.size())
                .housingType(notice.getHouseType())
                .complexes(complexesResponse)
                .build();
    }


}
