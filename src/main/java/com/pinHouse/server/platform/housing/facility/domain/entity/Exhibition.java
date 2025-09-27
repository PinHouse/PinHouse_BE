package com.pinHouse.server.platform.housing.facility.domain.entity;


import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Facility;
import com.pinHouse.server.platform.region.domain.entity.Location;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "exhibition")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exhibition implements Facility {

    @Id
    @Field("ID")
    private String id;

    @Field("LCLAS_NM")
    private String localClassName;

    @Field("MLSFC_NM")
    private String mainClassificationName;

    @Field("POI_ID")
    private long poiId;

    @Field("POI_NM")
    private String poiName;

    @Field("CL_CD")
    private int classCode;

    @Field("CL_NM")
    private String className;

    @Field("PNU")
    private long pnu;

    @Field("CTPRVN_NM")
    private String provinceName;

    @Field("SIGNGU_NM")
    private String cityName;

    @Field("LEGALDONG_NM")
    private String legalDongName;

    @Field("LNBR_NO")
    private String lotNumber;

    @Field("LEGALDONG_CD")
    private long legalDongCode;

    @Field("ADSTRD_CD")
    private long administrativeDistrictCode;

    @Field("RDNMADR_CD")
    private long roadNameAddressCode;

    @Field("RDNMADR_NM")
    private String roadNameAddress;

    @Field("BULD_NO")
    private int buildingNumber;

    @Field("LC_LO")
    private double longitude;

    @Field("LC_LA")
    private double latitude;

    @Field("ORIGIN_NM")
    private String originName;

    @Field("FILE_NM")
    private String fileName;

    @Field("BASE_DE")
    private int baseDate;

    @Field("location")
    private Location location;


}
