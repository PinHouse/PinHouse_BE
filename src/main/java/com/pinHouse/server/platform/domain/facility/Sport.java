package com.pinHouse.server.platform.domain.facility;

import com.pinHouse.server.platform.domain.location.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sport implements Facility{

    private String id;

    /** 시설 명칭 */
    private String name;

    /** 업종 명칭 */
    private String industryName;

    /** 시설 유형 명칭 */
    private String facilityTypeName;

    /** 시설 상태값 (예: 정상운영 등) */
    private String facilityStateValue;

    /** 도로명주소-1 */
    private String address;

    /// 좌표
    private Location location;

    /** 시설 운영 형태 값 */
    private String facilityOperStyleValue;

    /** 시설 기준일자(생성 기준일) */
    private String facilityCreationStandardDate;

    /** 국가 공공 시설 여부 */
    private String nationPublicFacilityAt;
}
