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
public class Animal implements Facility{

    /// 아이디
    private String id;

    /// 시설명
    private String name;

    /// 종류
    private String category;

    /// 좌표
    private Location location;

    /// 주소
    private String address;

    /// 휴무,운영
    private String restGuide;

    /// 운영시간
    private String operateTime;

    /// 가능여부
    private String petPossibleAt;

    /// 반려동물 크기
    private String enterPossiblePetSizeValue;

    /// 반려동물 제한사항
    private String petLimitMatterContent;

    /// 실내 입장 가능 여부
    private String inPlaceAcceptPossibleAt;

    /// 실외 입장 가능 여부
    private String outPlaceAcceptPossibleAt;

    /// 시설 안내
    private String facilityInfoDescription;

}
