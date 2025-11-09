package com.pinHouse.server.platform.search.domain.entity;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "search_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    private String pinPointId;

    private int transitTime;

    private double minSize;

    private double maxSize;

    private int maxDeposit;

    private int maxMonthPay;

    private List<FacilityType> facilities;

    private List<RentalType> rentalTypes;

    private List<SupplyType> supplyTypes;

    private List<HouseType> houseType;


    @Builder
    protected SearchHistory(
            String userId,
            String pinPointId,
            int transitTime,
            double minSize,
            double maxSize,
            int maxDeposit,
            int maxMonthPay,
            List<FacilityType> facilities,
            List<RentalType> rentalTypes,
            List<SupplyType> supplyTypes,
            List<HouseType> houseType
    ) {
        this.userId = userId;
        this.pinPointId = pinPointId;
        this.transitTime = transitTime;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.maxDeposit = maxDeposit;
        this.maxMonthPay = maxMonthPay;
        this.facilities = facilities;
        this.rentalTypes = rentalTypes;
        this.supplyTypes = supplyTypes;
        this.houseType = houseType;
    }


    /// 정적 팩토리 메서드
    public static SearchHistory of(
            String userId,
            String pinPointId,
            int transitTime,
            double minSize,
            double maxSize,
            int maxDeposit,
            int maxMonthPay,
            List<FacilityType> facilities,
            List<RentalType> rentalTypes,
            List<SupplyType> supplyTypes,
            List<HouseType> houseType
    ) {
        return SearchHistory.builder()
                .userId(userId)
                .pinPointId(pinPointId)
                .transitTime(transitTime)
                .minSize(minSize)
                .maxSize(maxSize)
                .maxDeposit(maxDeposit)
                .maxMonthPay(maxMonthPay)
                .facilities(facilities)
                .rentalTypes(rentalTypes)
                .supplyTypes(supplyTypes)
                .houseType(houseType)
                .build();
    }

}
