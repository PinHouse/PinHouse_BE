package com.pinHouse.server.platform.adapter.out.mongo.facility;

import com.pinHouse.server.platform.domain.facility.Animal;
import com.pinHouse.server.platform.domain.location.Location;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "animal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalDocument {

    /** MongoDB ObjectId */
    @Id
    @Field("_id")
    private String id;

    /** 시설명 */
    @Field("FCLTY_NM")
    private String facilityName;

    /** 3차 카테고리명 (ex. 펜션) */
    @Field("CTGRY_THREE_NM")
    private String categoryThreeName;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    /** 우편번호 */
    @Field("ZIP_NO")
    private Integer zipNo;

    /** 도로명주소 */
    @Field("RDNMADR_NM")
    private String roadAddressName;

    /** 휴무/운영 안내 */
    @Field("RSTDE_GUID_CN")
    private String restGuide;

    /** 운영시간 */
    @Field("OPER_TIME")
    private String operateTime;

    /** 반려동물 동반 가능 여부 */
    @Field("PET_POSBL_AT")
    private String petPossibleAt;

    /** 입장 가능 반려동물 크기 (ex. 7kg 미만) */
    @Field("ENTRN_POSBL_PET_SIZE_VALUE")
    private String enterPossiblePetSizeValue;

    /** 반려동물 제한사항 */
    @Field("PET_LMTT_MTR_CN")
    private String petLimitMatterContent;

    /** 실내 입장 가능 여부 */
    @Field("IN_PLACE_ACP_POSBL_AT")
    private String inPlaceAcceptPossibleAt;

    /** 실외 입장 가능 여부 */
    @Field("OUT_PLACE_ACP_POSBL_AT")
    private String outPlaceAcceptPossibleAt;

    /** 시설 안내/특징 */
    @Field("FCLTY_INFO_DC")
    private String facilityInfoDescription;

    /** 반려동물 추가 요금 안내 */
    @Field("PET_ACP_ADIT_CHRGE_VALUE")
    private String petAcceptAdditionalChargeValue;

    /// 도메인 변환
    public Animal toDomain(){
        return Animal.builder()
                .name(facilityName)
                .category(categoryThreeName)
                .location(Location.builder()
                        .type(location.getType())
                        .coordinates(location.getCoordinates())
                        .build())
                .address(roadAddressName)
                .restGuide(restGuide)
                .operateTime(operateTime)
                .petPossibleAt(petPossibleAt)
                .enterPossiblePetSizeValue(enterPossiblePetSizeValue)
                .petLimitMatterContent(petLimitMatterContent)
                .inPlaceAcceptPossibleAt(inPlaceAcceptPossibleAt)
                .outPlaceAcceptPossibleAt(outPlaceAcceptPossibleAt)
                .facilityInfoDescription(facilityInfoDescription)
                .build();
    }

}
