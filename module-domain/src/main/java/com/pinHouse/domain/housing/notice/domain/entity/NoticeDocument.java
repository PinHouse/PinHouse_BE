package com.pinHouse.domain.housing.notice.domain.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Field("thumbnail")
	private String thumbnail;

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

	@Field("targetGroup")
	private List<String> targetGroups;
}
