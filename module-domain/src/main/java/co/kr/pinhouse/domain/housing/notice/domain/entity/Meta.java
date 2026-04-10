package co.kr.pinhouse.domain.housing.notice.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meta {

	@Field("totalComplexCount")
	private int totalComplexCount;

	@Field("totalSupplyCount")
	private int totalSupplyCount;

	/// 빌더 생성자
	@Builder
	public Meta(int totalComplexCount, int totalSupplyCount) {
		this.totalComplexCount = totalComplexCount;
		this.totalSupplyCount = totalSupplyCount;
	}
}
