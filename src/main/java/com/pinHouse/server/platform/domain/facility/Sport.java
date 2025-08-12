package com.pinHouse.server.platform.domain.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sport {

    /** 시설 명칭 */
    private String facilityName;

    /** 업종 명칭 */
    private String industryName;

    /** 시설 유형 명칭 */
    private String facilityTypeName;

    /** 시설 상태값 (예: 정상운영 등) */
    private String facilityStateValue;

    /** 도로명주소-1 */
    private String roadAddress;

    /** 경도 */
    private Double facilityLongitude;

    /** 위도 */
    private Double facilityLatitude;

    /** 시설 운영 형태 값 */
    private String facilityOperStyleValue;

    /** 시설 기준일자(생성 기준일) */
    private String facilityCreationStandardDate;

    /** 국가 공공 시설 여부 */
    private String nationPublicFacilityAt;
}
