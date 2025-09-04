package com.pinHouse.server.platform.housing.facility.domain.entity;

import com.pinHouse.server.platform.region.domain.entity.Location;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Facility;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * MongoDB library document.
 */
@Document(collection = "library")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Library implements Facility {

    /**
     * MongoDB unique identifier (_id)
     */
    @Id
    @Field("_id")
    private String id;

    /**
     * Library code (도서관코드)
     */
    @Field("LBRRY_CD")
    private Integer code;

    /**
     * Library name (도서관명)
     */
    @Field("LBRRY_NM")
    private String name;

    /**
     * Library address (도서관주소)
     */
    @Field("LBRRY_ADDR")
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    /**
     * Province/State name (광역지역명)
     */
    @Field("ONE_AREA_NM")
    private String area;

    /**
     * City/District name (시군구명)
     */
    @Field("TWO_AREA_NM")
    private String city;

    /**
     * Library number (도서관번호)
     */
    @Field("LBRRY_NO")
    private Integer number;

    /**
     * Opening hours (운영시간)
     */
    @Field("OPNNG_TIME")
    private String openingTime;

    /**
     * Closed days (휴관일)
     */
    @Field("CLOSEDON_DC")
    private String closedDays;
}
