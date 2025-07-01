package com.myhome.server.platform.adapter.out.mongo.notice;

import com.myhome.server.platform.domain.location.Location;
import com.myhome.server.platform.domain.notice.Notice;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "notice")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDocument {

    @Id
    private String id;

    @Field("공고ID")
    private String noticeId;

    @Field("단지명")
    private String complexName;

    @Field("상태")
    private String status;

    @Field("시작일")
    private String startDate;

    @Field("유형")
    private String type;

    @Field("제목")
    private String title;

    @Field("조회수")
    private String views;

    @Field("종료일")
    private String endDate;

    @Field("주소")
    private String address;

    @Field("지역")
    private String region;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    @Field("complex")
    private NoticeComplexDocument complex;

    @Field("supply_info")
    private List<NoticeSupplyInfoDocument> supplyInfo;

    @Field("reception_center")
    private NoticeReceptionCenterDocument receptionCenter;

    @Field("notice_schedule")
    private NoticeScheduleDocument noticeSchedule;

    @Field("lease_conditions")
    private List<Map<String, Object>> leaseConditions;

    /// ToDomain
    public Notice toDomain() {

        Location location = Location.builder()
                .type("Point")
                .coordinates(Arrays.asList(
                        getLocation().getLongitude(),  // 경도
                        getLocation().getLatitude())   // 위도
                )
                .build();

        return Notice.builder()
                .id(noticeId)
                .noticeId(noticeId)
                .complexName(complexName)
                .status(status)
                .startDate(startDate)
                .type(type)
                .title(title)
                .views(views)
                .endDate(endDate)
                .address(address)
                .region(region)
                .location(location)
                .complex(complex.toDomain())
                .supplyInfo(supplyInfo.stream()
                                .map(NoticeSupplyInfoDocument::toDomain)
                        .toList()
                )
                .receptionCenter(receptionCenter.toDomain())
                .noticeSchedule(noticeSchedule.toDomain())
                .leaseConditions(leaseConditions)
                .build();

    }
}

