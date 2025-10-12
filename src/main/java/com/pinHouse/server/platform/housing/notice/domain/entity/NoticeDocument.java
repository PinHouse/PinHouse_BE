package com.pinHouse.server.platform.housing.notice.domain.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDocument {

    @Id
    private String id;

    @Field("noticeId")
    private String noticeId;

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
    private Date announceDate;

    @Field("winnerDate")
    private Date winnerDate;

    @Field("applyStart")
    private Date applyStart;

    @Field("applyEnd")
    private Date applyEnd;

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
    public NoticeDocument(String id, String noticeId, String status, String title, String agency,
                          String houseType, String supplyType, String prevNoticeId, Date announceDate,
                          Date winnerDate, Date applyStart, Date applyEnd,
                          String contact, Urls urls, String city, String county, Meta meta) {
        this.id = id;
        this.noticeId = noticeId;
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
