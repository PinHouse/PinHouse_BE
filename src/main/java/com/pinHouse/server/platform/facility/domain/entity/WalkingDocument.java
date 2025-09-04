package com.pinHouse.server.platform.facility.domain.entity;

import com.pinHouse.server.platform.facility.domain.Walking;
import com.pinHouse.server.platform.location.domain.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Document(collection = "walking")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkingDocument {

    /** MongoDB ObjectId */
    @Id
    @Field("_id")
    private String id;

    /** 코스 고유 식별자 */
    @Field("ESNTL_ID")
    private String esntlId;

    /** 걷기 코스 플래그명 (코스 대표 명칭) */
    @Field("WLK_COURS_FLAG_NM")
    private String walkingCourseFlagName;

    /** 걷기 코스명 */
    @Field("WLK_COURS_NM")
    private String walkingCourseName;

    /** 코스 상세설명 */
    @Field("COURS_DC")
    private String courseDescription;

    /** 행정구역명(시/군/구) */
    @Field("SIGNGU_NM")
    private String districtName;

    /** 코스 난이도 */
    @Field("COURS_LEVEL_NM")
    private String courseLevelName;

    /** 코스 거리 구간명 (예:10~15㎞미만) */
    @Field("COURS_LT_CN")
    private String courseLengthDescription;

    /** 상세 거리 (단위: km, 실수 값) */
    @Field("COURS_DETAIL_LT_CN")
    private Double courseDetailLengthKm;

    /** 추가 설명 (코스 특성, 지역 건강증진 등) */
    @Field("ADIT_DC")
    private String additionalDescription;

    /** 예상 소요 시간 (예: 4시간) */
    @Field("COURS_TIME_CN")
    private String courseTime;

    /** 편의시설 및 옵션 안내 */
    @Field("OPTN_DC")
    private String optionDescription;

    /** 화장실 안내 */
    @Field("TOILET_DC")
    private String toiletDescription;

    /** 주변 편의점/휴게시설 안내 */
    @Field("CVNTL_NM")
    private String convenienceName;

    /** 코스 시작/대표 주소 */
    @Field("LNM_ADDR")
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    /// toDomain
    public Walking toDomain() {
        return Walking.builder()
                .id(id)
                .esntlId(esntlId)
                .walkingCourseFlagName(walkingCourseFlagName)
                .walkingCourseName(walkingCourseName)
                .courseDescription(courseDescription)
                .districtName(districtName)
                .courseLevelName(courseLevelName)
                .courseLengthDescription(courseLengthDescription)
                .courseDetailLengthKm(courseDetailLengthKm)
                .courseLevelName(courseLevelName)
                .courseLengthDescription(courseLengthDescription)
                .courseDetailLengthKm(courseDetailLengthKm)
                .courseTime(courseTime)
                .optionDescription(optionDescription)
                .toiletDescription(toiletDescription)
                .convenienceName(convenienceName)
                .address(address)
                .location(Location.builder()
                        .type(location.getType())
                        .coordinates(location.getCoordinates())
                        .build())
                .build();
    }

}
