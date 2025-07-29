package com.pinHouse.server.platform.adapter.out.mongo.notice;

import com.pinHouse.server.platform.domain.location.Location;
import com.pinHouse.server.platform.domain.notice.Notice;
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
@Document(collection = "home")
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

    @Field("상태명")
    private String status;

    @Field("모집시작일자")
    private String startDate;

    @Field("공급유형명")
    private String type;

    @Field("공고명")
    private String title;

    @Field("공급기관명")
    private String supplier;

    @Field("조회수")
    private String views; // 원 데이터에는 없음

    @Field("모집종료일자")
    private String endDate;

    @Field("전체주소")
    private String address;

    @Field("광역시도명")
    private String region;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;

    @Field("공급정보목록")
    private List<NoticeSupplyInfoDocument> supplyInfo;

    @Field("모집공고URL")
    private String noticeUrl;

    @Field("마이홈포털PCURL")
    private String myHomePcUrl;

    @Field("마이홈포털MobileURL")
    private String myHomeMobileUrl;

    @Field("문의처")
    private String contact;

    @Field("당첨자발표일자")
    private String winnerAnnouncementDate;

    @Field("난방방식명")
    private String heatingMethod;

    @Field("총세대수")
    private String totalHouseholds;

    public Notice toDomain() {
        return Notice.builder()
                .id(noticeId)
                .noticeId(noticeId)
                .complexName(complexName)
                .status(status)
                .startDate(startDate)
                .supplier(supplier)
                .type(type)
                .title(title)
                .views(views)
                .endDate(endDate)
                .address(address)
                .region(region)
                .location(Location.builder()
                        .type(location.getType())
                        .coordinates(location.getCoordinates())
                        .build())
                .supplyInfo(supplyInfo.stream()
                        .map(NoticeSupplyInfoDocument::toDomain)
                        .toList())
                .noticeUrl(noticeUrl)
                .myHomePcUrl(myHomePcUrl)
                .myHomeMobileUrl(myHomeMobileUrl)
                .contact(contact)
                .winnerAnnouncementDate(winnerAnnouncementDate)
                .heatingMethod(heatingMethod)
                .totalHouseholds(totalHouseholds)
                .build();
    }
}

