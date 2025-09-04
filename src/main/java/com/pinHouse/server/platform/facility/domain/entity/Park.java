package com.pinHouse.server.platform.facility.domain.entity;

import com.pinHouse.server.core.entity.Location;
import com.pinHouse.server.platform.facility.domain.entity.infra.Facility;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MongoDB 공원 도큐멘트
 */

@Document(collection = "park")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Park implements Facility {

    @Id
    @Field("_id")
    private String id;

    /** 공원 시스템 ID */
    @Field("ID")
    private String parkId;

    /** POI 명칭 */
    @Field("POI_NM")
    private String name;

    /** 공원 분류 명칭 */
    @Field("CL_NM")
    private String category;

    /** PNU (법정동+지번코드) */
    @Field("PNU")
    private String pnu;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

}
