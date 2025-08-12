package com.pinHouse.server.platform.domain.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Park {

    /** 공원 시스템 ID */
    private String parkId;

    /** POI 명칭 */
    private String poiName;

    /** 공원 분류 명칭 */
    private String categoryName;

    /** PNU (법정동+지번코드) */
    private String pnu;

    /** 도로명주소명 */
    private String roadAddressName;

    /** 경도 */
    private Double longitude;

    /** 위도 */
    private Double latitude;

}
