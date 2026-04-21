package co.kr.pinhouse.domain.ad.domain.entity;

import java.time.LocalDateTime;

import co.kr.pinhouse.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "advertisements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertisement extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AdvertisementStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private AdvertisementPlacement placement;

	@Column(nullable = false)
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AdvertisementLinkType linkType;

	private String linkValue;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@Column(nullable = false)
	private int priority;

	@Builder
	protected Advertisement(
		String title,
		AdvertisementStatus status,
		AdvertisementPlacement placement,
		String imageUrl,
		AdvertisementLinkType linkType,
		String linkValue,
		LocalDateTime startAt,
		LocalDateTime endAt,
		int priority
	) {
		this.title = title;
		this.status = status;
		this.placement = placement;
		this.imageUrl = imageUrl;
		this.linkType = linkType;
		this.linkValue = linkValue;
		this.startAt = startAt;
		this.endAt = endAt;
		this.priority = priority;
	}

	public static Advertisement create(
		String title,
		AdvertisementPlacement placement,
		String imageUrl,
		AdvertisementLinkType linkType,
		String linkValue,
		LocalDateTime startAt,
		LocalDateTime endAt,
		int priority
	) {
		return Advertisement.builder()
			.title(title)
			.status(AdvertisementStatus.DRAFT)
			.placement(placement)
			.imageUrl(imageUrl)
			.linkType(linkType)
			.linkValue(linkValue)
			.startAt(startAt)
			.endAt(endAt)
			.priority(priority)
			.build();
	}

	public void update(
		String title,
		AdvertisementPlacement placement,
		String imageUrl,
		AdvertisementLinkType linkType,
		String linkValue,
		LocalDateTime startAt,
		LocalDateTime endAt,
		Integer priority
	) {
		if (title != null) {
			this.title = title;
		}
		if (placement != null) {
			this.placement = placement;
		}
		if (imageUrl != null) {
			this.imageUrl = imageUrl;
		}
		if (linkType != null) {
			this.linkType = linkType;
		}
		if (linkValue != null) {
			this.linkValue = linkValue;
		}
		if (startAt != null) {
			this.startAt = startAt;
		}
		if (endAt != null) {
			this.endAt = endAt;
		}
		if (priority != null) {
			this.priority = priority;
		}
	}

	public void changeStatus(AdvertisementStatus status) {
		this.status = status;
	}

	public boolean isExposedAt(LocalDateTime now) {
		if (status != AdvertisementStatus.ACTIVE) {
			return false;
		}
		if (startAt != null && now.isBefore(startAt)) {
			return false;
		}
		if (endAt != null && now.isAfter(endAt)) {
			return false;
		}
		return true;
	}
}
