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
public class Walking {

    private String id;

    /// 코스 고유 식별자
    private String esntlId;

    ///걷기 코스 플래그명 (코스 대표 명칭)
    private String walkingCourseFlagName;

    /// 걷기 코스명
    private String walkingCourseName;

    /// 코스 상세설명 */
    private String courseDescription;

    /// 행정구역명(시/군/구) */
    private String districtName;

    /// 코스 난이도 */
    private String courseLevelName;

    /// 코스 거리 구간명 (예: 10~15㎞미만) */
    private String courseLengthDescription;

    /// 상세 거리 (단위: km, 실수 값) */
    private Double courseDetailLengthKm;

    /** 추가 설명 (코스 특성, 지역 건강증진 등) */
    private String additionalDescription;

    /** 예상 소요 시간 (예: 4시간) */
    private String courseTime;

    /** 편의시설 및 옵션 안내 */
    private String optionDescription;

    /** 화장실 안내 */
    private String toiletDescription;

    /** 주변 편의점/휴게시설 안내 */
    private String convenienceName;

    /** 코스 시작/대표 주소 */
    private String address;

    /// 좌표
    private Location location;
}
