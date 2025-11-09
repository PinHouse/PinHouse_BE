package com.pinHouse.server.platform.housing.notice.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDocument {

    @Field("noticeId")
    private String id;

    @Field("status")
    private String status;

    @Field("title")
    private String title;

    @Field("agency")
    private String agency;

    @Field("houseType")
    private String houseType;

    @Field("supplyType")
    private String supplyType;

    @Field("prevNoticeId")
    private String prevNoticeId;

    @Field("announceDate")
    private LocalDate announceDate;

    @Field("winnerDate")
    private LocalDate winnerDate;

    @Field("applyStart")
    private LocalDate applyStart;

    @Field("applyEnd")
    private LocalDate applyEnd;

    @Field("contact")
    private String contact;

    @Field("urls")
    private Urls urls;

    @Field("city")
    private String city;

    @Field("county")
    private String county;

    @Field("meta")
    private Meta meta;

    /// 빌더 생성자
    @Builder
    public NoticeDocument(String id, String status, String title, String agency,
                          String houseType, String supplyType, String prevNoticeId, LocalDate announceDate,
                          LocalDate winnerDate, LocalDate applyStart, LocalDate applyEnd,
                          String contact, Urls urls, String city, String county, Meta meta) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.agency = agency;
        this.houseType = houseType;
        this.supplyType = supplyType;
        this.prevNoticeId = prevNoticeId;
        this.announceDate = announceDate;
        this.winnerDate = winnerDate;
        this.applyStart = applyStart;
        this.applyEnd = applyEnd;
        this.contact = contact;
        this.urls = urls;
        this.city = city;
        this.county = county;
        this.meta = meta;
    }




}
