package com.pinHouse.domain.housing.complex.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
	@Field("full")
	private String full;

	@Field("road")
	private String road;

	/// 빌더 생성자
	@Builder
	public Address(String full, String road) {
		this.full = full;
		this.road = road;
	}
}
