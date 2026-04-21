package co.kr.pinhouse.domain.ad.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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
@Table(name = "advertisement_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdvertisementEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "advertisement_id", nullable = false)
	private Advertisement advertisement;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AdvertisementEventType eventType;

	@Column(name = "user_id", columnDefinition = "BINARY(16)")
	private UUID userId;

	@Column(name = "client_ip", length = 100)
	private String clientIp;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	@Builder
	protected AdvertisementEvent(
		Advertisement advertisement,
		AdvertisementEventType eventType,
		UUID userId,
		String clientIp,
		LocalDateTime occurredAt
	) {
		this.advertisement = advertisement;
		this.eventType = eventType;
		this.userId = userId;
		this.clientIp = clientIp;
		this.occurredAt = occurredAt;
	}

	public static AdvertisementEvent of(
		Advertisement advertisement,
		AdvertisementEventType eventType,
		UUID userId,
		String clientIp
	) {
		return AdvertisementEvent.builder()
			.advertisement(advertisement)
			.eventType(eventType)
			.userId(userId)
			.clientIp(clientIp)
			.occurredAt(LocalDateTime.now())
			.build();
	}
}
