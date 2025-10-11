package com.pinHouse.server.platform.diagnostic.rule.domain.entity;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * 진단을 위해서 공급/주택유형을 1:1 매핑으로 걸어두는 것
 */

@Builder
public record SupplyRentalCandidate(
        SupplyType supplyType,
        NoticeType noticeType
) {

    /// 정적 팩토리 메서드 (기본 값)
    public static List<SupplyRentalCandidate> basic() {

        /// 모든 값 가져오기
        List<SupplyType> supplyTypes = SupplyType.getAllTypes();

        /// 모든 값 가져오기
        List<NoticeType> noticeTypes = NoticeType.getAllTypes();

        List<SupplyRentalCandidate> candidates = new ArrayList<>();

        /// for문 돌리면서 값 합치기
        for (NoticeType rental : NoticeType.getAllTypes()) {

            /// 매핑 되는 Supply 가져오기
            List<SupplyType> allowedSupplies = RentalSupplyMapping.RENTAL_SUPPLY_MAP.get(rental);

            /// 매핑 없는 경우 무시
            if (allowedSupplies == null) continue;
            for (SupplyType supply : allowedSupplies) {

                /// 더하기
                candidates.add(SupplyRentalCandidate.basic(supply, rental));
            }
        }

        return candidates;
    }

    /// 정적 팩토리 메서드
    public static SupplyRentalCandidate basic(SupplyType supplyType, NoticeType noticeType) {
        return SupplyRentalCandidate.builder()
                .supplyType(supplyType)
                .noticeType(noticeType)
                .build();
    }


}
