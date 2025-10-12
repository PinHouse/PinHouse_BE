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

    @Id
    private String id;

    @Field("noticeId")
    private String noticeId;

    @Field("houseSn")
    private int houseSn;

    @Field("complexKey")
    private String complexKey;

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
    public ComplexDocument(String id, String noticeId, int houseSn, String complexKey, String name,
                           Address address, String pnu, String city, String county, String heating,
                           String totalHouseholds, int totalSupplyInNotice, String applyStart,
                           String applyEnd, Location location, List<UnitType> unitTypes) {
        this.id = id;
        this.noticeId = noticeId;
        this.houseSn = houseSn;
        this.complexKey = complexKey;
        this.name = name;
        this.address = address;
        this.pnu = pnu;
        this.city = city;
        this.county = county;
        this.heating = heating;
        this.totalHouseholds = totalHouseholds;
        this.totalSupplyInNotice = totalSupplyInNotice;
        this.applyStart = applyStart;
        this.applyEnd = applyEnd;
        this.location = location;
        this.unitTypes = unitTypes;
    }

}
