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

@Document(collection = "mart")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mart implements Facility {

    @Id
    private String id;

    @Field("번호")
    private int number;

    @Field("개방서비스명")
    private String openServiceName;

    @Field("개방자치단체코드")
    private int localGovCode;

    @Field("관리번호")
    private String managementNumber;

    @Field("영업상태구분코드")
    private int businessStatusCode;

    @Field("소재지면적")
    private double area;

    @Field("소재지전체주소")
    private String fullAddress;

    @Field("도로명전체주소")
    private String roadFullAddress;

    @Field("사업장명")
    private String businessName;

    @Field("업태구분명")
    private String businessType;

    @Field("점포구분명")
    private String storeType;

    @Field("location")
    private Location location;


}
