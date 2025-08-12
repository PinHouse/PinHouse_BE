package com.pinHouse.server.platform.domain.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animal {

    /**
     * 시설명
     */
    private String facilityName;

    /**
     * 3차 카테고리명 (ex. 펜션)
     */
    private String categoryThreeName;

    /**
     * 위도
     */
    private Double latitude;

    /**
     * 경도
     */
    private Double longitude;

    /**
     * 우편번호
     */
    private Integer zipNo;

    /**
     * 도로명주소
     */
    private String roadAddressName;

    /**
     * 휴무/운영 안내
     */
    private String restGuide;

    /**
     * 운영시간
     */
    private String operateTime;

    /**
     * 반려동물 동반 가능 여부
     */
    private String petPossibleAt;

    /**
     * 반려동물 정보
     */
    private String petInfoContent;

    /**
     * 입장 가능 반려동물 크기 (ex. 7kg 미만)
     */
    private String enterPossiblePetSizeValue;

    /**
     * 반려동물 제한사항
     */
    private String petLimitMatterContent;

    /**
     * 실내 입장 가능 여부
     */
    private String inPlaceAcceptPossibleAt;

    /**
     * 실외 입장 가능 여부
     */
    private String outPlaceAcceptPossibleAt;

    /**
     * 시설 안내/특징
     */
    private String facilityInfoDescription;

    /**
     * 반려동물 추가 요금 안내
     */
    private String petAcceptAdditionalChargeValue;

}
