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

@Document(collection = "sports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sport implements Facility {

    /** MongoDB ObjectId */
    @Id
    @Field("_id")
    private String id;

    /** 시설 명칭 */
    @Field("FCLTY_NM")
    private String name;

    /** 업종 명칭 */
    @Field("INDUTY_NM")
    private String industryName;

    /** 시설 유형 명칭 */
    @Field("FCLTY_TY_NM")
    private String facilityTypeName;

    /** 시설 상태값 (예: 정상운영 등) */
    @Field("FCLTY_STATE_VALUE")
    private String facilityStateValue;

    /** 도로명주소-1 */
    @Field("RDNMADR_ONE_NM")
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    /** 시설 운영 형태 값 */
    @Field("FCLTY_OPER_STLE_VALUE")
    private String facilityOperStyleValue;

    /** 시설 기준일자(생성 기준일) */
    @Field("FCLTY_CRTN_STDR_DE")
    private String facilityCreationStandardDate;

    /** 국가 공공 시설 여부 */
    @Field("NATION_ALSFC_AT")
    private String nationPublicFacilityAt;


    /// toDomain
    public Sport toDomain() {
        return Sport.builder()
                .id(id)
                .name(name)
                .industryName(industryName)
                .facilityTypeName(facilityTypeName)
                .facilityStateValue(facilityStateValue)
                .address(address)
                .location(Location.builder()
                        .type(location.getType())
                        .coordinates(location.getCoordinates())
                        .build())
                .facilityOperStyleValue(facilityOperStyleValue)
                .facilityCreationStandardDate(facilityCreationStandardDate)
                .nationPublicFacilityAt(nationPublicFacilityAt)
                .build();

    }
}
