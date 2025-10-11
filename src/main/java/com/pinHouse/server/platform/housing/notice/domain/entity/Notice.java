package com.pinHouse.server.platform.housing.notice.domain.entity;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.deposit.domain.entity.NoticeSupply;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
@Document(collection = "home")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

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
    private List<NoticeSupply> supplyInfo;

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

    /// 빌더 생성자
    @Builder
    public Notice(String id, String noticeId, String complexName, String status, String startDate,
                  String type, String title, String supplier, String views, String endDate,
                  String address, String region, Location location, List<NoticeSupply> supplyInfo,
                  String noticeUrl, String myHomePcUrl, String myHomeMobileUrl, String contact,
                  String winnerAnnouncementDate, String heatingMethod, String totalHouseholds) {
        this.id = id;
        this.noticeId = noticeId;
        this.complexName = complexName;
        this.status = status;
        this.startDate = startDate;
        this.type = type;
        this.title = title;
        this.supplier = supplier;
        this.views = views;
        this.endDate = endDate;
        this.address = address;
        this.region = region;
        this.location = location;
        this.supplyInfo = supplyInfo;
        this.noticeUrl = noticeUrl;
        this.myHomePcUrl = myHomePcUrl;
        this.myHomeMobileUrl = myHomeMobileUrl;
        this.contact = contact;
        this.winnerAnnouncementDate = winnerAnnouncementDate;
        this.heatingMethod = heatingMethod;
        this.totalHouseholds = totalHouseholds;
    }
}

