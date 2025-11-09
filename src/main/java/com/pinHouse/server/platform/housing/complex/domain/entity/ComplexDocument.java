package com.pinHouse.server.platform.housing.complex.domain.entity;

import com.pinHouse.server.platform.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "complexes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComplexDocument {

    @Field("complexId")
    private String id;

    @Field("noticeId")
    private String noticeId;

    @Field("houseSn")
    private int houseSn;

    @Field("name")
    private String name;

    @Field("address")
    private Address address;

    @Field("pnu")
    private String pnu;

    @Field("city")
    private String city;

    @Field("county")
    private String county;

    @Field("heating")
    private String heating;

    @Field("totalHouseholds")
    private String totalHouseholds;

    @Field("totalSupplyInNotice")
    private int totalSupplyInNotice;

    @Field("applyStart")
    private String applyStart;

    @Field("applyEnd")
    private String applyEnd;

    @Field("location")
    private Location location;

    @Field("unitTypes")
    private List<UnitType> unitTypes;


    /// 빌더 생성자
    @Builder
    public ComplexDocument(ComplexDocument src, List<UnitType> unitTypes) {
        this.id = src.getId();
        this.noticeId = src.getNoticeId();
        this.houseSn = src.getHouseSn();
        this.name = src.getName();
        this.address = src.getAddress();
        this.pnu = src.getPnu();
        this.city = src.getCity();
        this.county = src.getCounty();
        this.heating = src.getHeating();
        this.totalHouseholds = src.getTotalHouseholds();
        this.totalSupplyInNotice = src.getTotalSupplyInNotice();
        this.applyStart = src.getApplyStart();
        this.applyEnd = src.getApplyEnd();
        this.location = src.getLocation();

        // 필터링된 리스트만 교체
        this.unitTypes = unitTypes;
    }


}
