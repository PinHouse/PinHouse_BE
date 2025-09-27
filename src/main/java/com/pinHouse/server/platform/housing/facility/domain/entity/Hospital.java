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

@Document(collection = "hospital")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital implements Facility {

    @Id
    private String id;

    @Field("번호")
    private int number;

    @Field("개방서비스명")
    private String serviceName;

    @Field("관리번호")
    private String managementNumber;

    @Field("소재지전화")
    private String phone;

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

    @Field("의료기관종별명")
    private String medicalInstitutionType;

    @Field("의료인수")
    private int medicalStaffCount;

    @Field("입원실수")
    private int inpatientRoomCount;

    @Field("병상수")
    private int bedCount;

    @Field("총면적")
    private double totalArea;

    @Field("진료과목내용")
    private String medicalSubjectCodes;

    @Field("진료과목내용명")
    private String medicalSubjectNames;

    @Field("관리주체")
    private String managingEntity;

    @Field("location")
    private Location location;

}
