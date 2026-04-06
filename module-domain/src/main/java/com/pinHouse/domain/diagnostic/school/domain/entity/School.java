package com.pinHouse.domain.diagnostic.school.domain.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "school")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School {

	@Id
	private String id;

	@Column(nullable = false)
	private String schoolName;

	@Column(nullable = false)
	private String schoolType;

	@Column(nullable = false)
	private String schoolCategory;

	@Column(nullable = false)
	private String sidoCode;

	@Column(nullable = false)
	private String sidoName;

	@Column(nullable = false)
	private String sigunguName;

	/// 빌더 생성자
	@Builder
	public School(String schoolName, String schoolType, String schoolCategory, String sidoCode, String sidoName, String sigunguName) {
		this.id = UUID.randomUUID().toString();
		this.schoolName = schoolName;
		this.schoolType = schoolType;
		this.schoolCategory = schoolCategory;
		this.sidoCode = sidoCode;
		this.sidoName = sidoName;
		this.sigunguName = sigunguName;
	}
}
