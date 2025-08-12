package com.pinHouse.server.platform.adapter.out.mongo.facility;

import com.pinHouse.server.platform.domain.facility.Park;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MongoDB 공원 도큐멘트
 */

@Document(collection = "park")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkDocument {

    @Id
    @Field("_id")
    private String id;

    /** 공원 시스템 ID */
    @Field("ID")
    private String parkId;

    /** POI 명칭 */
    @Field("POI_NM")
    private String poiName;

    /** 공원 분류 명칭 */
    @Field("CL_NM")
    private String categoryName;

    /** PNU (법정동+지번코드) */
    @Field("PNU")
    private String pnu;

    /** 도로명주소명 */
    @Field("RDNMADR_NM")
    private String roadAddressName;

    /** 경도 */
    @Field("LC_LO")
    private Double longitude;

    /** 위도 */
    @Field("LC_LA")
    private Double latitude;

    /// 도메인 변환
    private Park toDomain() {
        return Park.builder()
                .parkId(this.parkId)
                .poiName(this.poiName)
                .categoryName(this.categoryName)
                .pnu(this.pnu)
                .roadAddressName(this.roadAddressName)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .build();
    }

}
