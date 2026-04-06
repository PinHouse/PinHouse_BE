package co.kr.pinhouse.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
	private String type;
	private List<Double> coordinates;

	/// 정적 팩토리 메서드
	public static Location of(Double longitude, Double latitude) {
		return Location.builder()
			.type("Point")
			.coordinates(List.of(longitude, latitude))
			.build();
	}

	@JsonIgnore
	public Double getLongitude() {
		return coordinates.get(0);
	}

	@JsonIgnore
	public Double getLatitude() {
		return coordinates.get(1);
	}
}
