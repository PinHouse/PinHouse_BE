package co.kr.pinhouse.domain.housing.complex.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quota {

	@Field("total")
	private int total;

	@Field("priority")
	private int priority;

	@Field("general")
	private int general;

	/// 빌더 생성자
	@Builder
	public Quota(int total, int priority, int general) {
		this.total = total;
		this.priority = priority;
		this.general = general;
	}
}
