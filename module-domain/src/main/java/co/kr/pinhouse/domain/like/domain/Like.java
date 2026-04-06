package co.kr.pinhouse.domain.like.domain;

import co.kr.pinhouse.domain.BaseTimeEntity;
import co.kr.pinhouse.domain.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Column(nullable = false)
	private String targetId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LikeType type;

	/// 빌더 생성자
	@Builder
	protected Like(User user, String targetId, LikeType type) {
		this.user = user;
		this.targetId = targetId;
		this.type = type;
	}

	/// 정적 팩토리 메서드
	public static Like of(User user, String targetId, LikeType type) {
		return Like.builder()
			.user(user)
			.targetId(targetId)
			.type(type)
			.build();
	}

}
