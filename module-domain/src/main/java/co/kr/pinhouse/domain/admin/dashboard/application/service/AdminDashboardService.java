package co.kr.pinhouse.domain.admin.dashboard.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.domain.ad.domain.entity.Advertisement;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementEventType;
import co.kr.pinhouse.domain.ad.domain.entity.AdvertisementStatus;
import co.kr.pinhouse.domain.ad.domain.repository.AdvertisementEventRepository;
import co.kr.pinhouse.domain.ad.domain.repository.AdvertisementRepository;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import co.kr.pinhouse.domain.admin.dashboard.application.dto.response.AdminDashboardResponse;
import co.kr.pinhouse.domain.admin.dashboard.application.usecase.AdminDashboardUseCase;
import co.kr.pinhouse.domain.admin.notice.domain.repository.NoticeAdminOverrideRepository;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import co.kr.pinhouse.domain.cs.domain.repository.CsInquiryRepository;
import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.repository.DiagnosisJpaRepository;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.entity.Urls;
import co.kr.pinhouse.domain.housing.notice.domain.repository.NoticeDocumentRepository;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService implements AdminDashboardUseCase {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");
	private static final String TIMEZONE = "Asia/Seoul";
	private static final int URGENT_CS_THRESHOLD_MINUTES = 60;
	private static final int DELAYED_CS_THRESHOLD_HOURS = 24;
	private static final int ADS_ENDING_SOON_DAYS = 7;

	private static final List<CsInquiryStatus> RESOLVED_CS_STATUSES = List.of(
		CsInquiryStatus.RESOLVED,
		CsInquiryStatus.CLOSED
	);
	private static final List<CsInquiryStatus> URGENT_CS_STATUSES = List.of(
		CsInquiryStatus.RECEIVED,
		CsInquiryStatus.IN_PROGRESS
	);

	private final AdminSessionUseCase adminSessionService;
	private final UserJpaRepository userRepository;
	private final CsInquiryRepository inquiryRepository;
	private final DiagnosisJpaRepository diagnosisRepository;
	private final AdvertisementRepository advertisementRepository;
	private final AdvertisementEventRepository advertisementEventRepository;
	private final NoticeDocumentRepository noticeRepository;
	private final NoticeAdminOverrideRepository overrideRepository;

	@Override
	public AdminDashboardResponse getDashboard(UUID adminId) {
		adminSessionService.loadAdmin(adminId);

		ZonedDateTime now = ZonedDateTime.now(KST);
		LocalDate today = now.toLocalDate();
		LocalDateTime nowLocal = now.toLocalDateTime();

		DateTimeRange todayRange = DateTimeRange.of(today.atStartOfDay(), nowLocal);
		DateTimeRange yesterdayComparableRange = todayRange.shiftDays(-1);
		DateTimeRange currentWeekRange = DateTimeRange.of(
			today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(),
			nowLocal
		);
		DateTimeRange previousWeekComparableRange = currentWeekRange.shiftWeeks(-1);
		DateTimeRange recentSevenDaysRange = DateTimeRange.of(today.minusDays(6).atStartOfDay(), nowLocal);
		DateTimeRange previousSevenDaysRange = recentSevenDaysRange.shiftDays(-7);
		DateTimeRange currentMonthRange = DateTimeRange.of(today.withDayOfMonth(1).atStartOfDay(), nowLocal);
		DateTimeRange previousMonthComparableRange = currentMonthRange.shiftMonths(-1);

		return AdminDashboardResponse.builder()
			.asOf(now.toOffsetDateTime())
			.timezone(TIMEZONE)
			.statCards(buildStatCards(
				nowLocal,
				currentWeekRange,
				previousWeekComparableRange,
				recentSevenDaysRange,
				previousSevenDaysRange,
				currentMonthRange,
				previousMonthComparableRange
			))
			.todayBrief(buildTodayBrief(nowLocal, todayRange, yesterdayComparableRange))
			.recentCs(buildRecentCs())
			.weeklyTrend(buildWeeklyTrend(today, todayRange))
			.checkpoints(buildCheckpoints(today, nowLocal))
			.build();
	}

	private AdminDashboardResponse.StatCards buildStatCards(
		LocalDateTime now,
		DateTimeRange currentWeekRange,
		DateTimeRange previousWeekRange,
		DateTimeRange recentSevenDaysRange,
		DateTimeRange previousSevenDaysRange,
		DateTimeRange currentMonthRange,
		DateTimeRange previousMonthRange
	) {
		return new AdminDashboardResponse.StatCards(
			buildTotalUsersCard(currentWeekRange, previousWeekRange),
			buildCsInquiriesCard(recentSevenDaysRange, previousSevenDaysRange),
			buildActiveAdvertisementsCard(now, recentSevenDaysRange),
			buildMonthlyDiagnosesCard(currentMonthRange, previousMonthRange)
		);
	}

	private AdminDashboardResponse.DashboardMetric buildTotalUsersCard(
		DateTimeRange currentWeekRange,
		DateTimeRange previousWeekRange
	) {
		long totalUsers = userRepository.count();
		long currentWeekNewUsers = countUsers(currentWeekRange);
		long previousWeekNewUsers = countUsers(previousWeekRange);

		return new AdminDashboardResponse.DashboardMetric(
			totalUsers,
			currentWeekNewUsers - previousWeekNewUsers,
			calculateChangePercent(currentWeekNewUsers, previousWeekNewUsers),
			"주간 신규 vs 지난주",
			BigDecimal.valueOf(currentWeekNewUsers),
			"이번 주 신규"
		);
	}

	private AdminDashboardResponse.DashboardMetric buildCsInquiriesCard(
		DateTimeRange recentSevenDaysRange,
		DateTimeRange previousSevenDaysRange
	) {
		long recentSevenDaysCs = countCsInquiries(recentSevenDaysRange);
		long previousSevenDaysCs = countCsInquiries(previousSevenDaysRange);
		long unresolvedCs = inquiryRepository.countByStatusNotIn(RESOLVED_CS_STATUSES);

		return new AdminDashboardResponse.DashboardMetric(
			recentSevenDaysCs,
			recentSevenDaysCs - previousSevenDaysCs,
			calculateChangePercent(recentSevenDaysCs, previousSevenDaysCs),
			"최근 7일 vs 이전 7일",
			BigDecimal.valueOf(unresolvedCs),
			"미처리"
		);
	}

	private AdminDashboardResponse.DashboardMetric buildActiveAdvertisementsCard(
		LocalDateTime now,
		DateTimeRange recentSevenDaysRange
	) {
		List<Advertisement> activeStatusAdvertisements = advertisementRepository.findByStatus(AdvertisementStatus.ACTIVE);
		long activeNow = activeStatusAdvertisements.stream()
			.filter(advertisement -> advertisement.isExposedAt(now))
			.count();
		long activeWeekAgo = activeStatusAdvertisements.stream()
			.filter(advertisement -> advertisement.isExposedAt(now.minusWeeks(1)))
			.count();

		long weeklyClicks = countAdvertisementEvents(AdvertisementEventType.CLICK, recentSevenDaysRange);
		long weeklyImpressions = countAdvertisementEvents(AdvertisementEventType.IMPRESSION, recentSevenDaysRange);

		return new AdminDashboardResponse.DashboardMetric(
			activeNow,
			activeNow - activeWeekAgo,
			calculateChangePercent(activeNow, activeWeekAgo),
			"활성 수 vs 지난주",
			calculateRate(weeklyClicks, weeklyImpressions),
			"최근 7일 CTR"
		);
	}

	private AdminDashboardResponse.DashboardMetric buildMonthlyDiagnosesCard(
		DateTimeRange currentMonthRange,
		DateTimeRange previousMonthRange
	) {
		long currentMonthDiagnoses = countDiagnoses(currentMonthRange);
		long previousMonthDiagnoses = countDiagnoses(previousMonthRange);
		long currentMonthNewUsers = countUsers(currentMonthRange);
		long currentMonthDiagnosedNewUsers = diagnosisRepository.countDistinctUsersByCreatedAtBetweenAndUserCreatedAtBetween(
			currentMonthRange.start(),
			currentMonthRange.end(),
			currentMonthRange.start(),
			currentMonthRange.end()
		);

		return new AdminDashboardResponse.DashboardMetric(
			currentMonthDiagnoses,
			currentMonthDiagnoses - previousMonthDiagnoses,
			calculateChangePercent(currentMonthDiagnoses, previousMonthDiagnoses),
			"전월 동기간 대비",
			calculateRate(currentMonthDiagnosedNewUsers, currentMonthNewUsers),
			"신규 가입 진단 완료율"
		);
	}

	private AdminDashboardResponse.TodayBrief buildTodayBrief(
		LocalDateTime now,
		DateTimeRange todayRange,
		DateTimeRange yesterdayComparableRange
	) {
		long newUsersToday = countUsers(todayRange);
		long diagnosesToday = countDiagnoses(todayRange);
		long diagnosedNewUsersToday = diagnosisRepository.countDistinctUsersByCreatedAtBetweenAndUserCreatedAtBetween(
			todayRange.start(),
			todayRange.end(),
			todayRange.start(),
			todayRange.end()
		);

		return new AdminDashboardResponse.TodayBrief(
			inquiryRepository.countByStatusInAndLastMessageAtBefore(
				URGENT_CS_STATUSES,
				now.minusMinutes(URGENT_CS_THRESHOLD_MINUTES)
			),
			URGENT_CS_THRESHOLD_MINUTES,
			newUsersToday,
			newUsersToday - countUsers(yesterdayComparableRange),
			diagnosesToday,
			calculateRate(diagnosedNewUsersToday, newUsersToday)
		);
	}

	private List<AdminDashboardResponse.RecentCsItem> buildRecentCs() {
		List<CsInquiry> recentInquiries = inquiryRepository.findTop5ByOrderByLastMessageAtDescIdDesc();
		Map<UUID, String> adminNames = loadAdminNames(recentInquiries);

		return recentInquiries.stream()
			.map(inquiry -> new AdminDashboardResponse.RecentCsItem(
				inquiry.getId(),
				inquiry.getTitle(),
				maskName(inquiry.getUser().getName()),
				inquiry.getStatus().name(),
				toOffsetDateTime(inquiry.getCreatedAt()),
				toOffsetDateTime(inquiry.getLastMessageAt()),
				adminNames.get(inquiry.getAssignedAdminId())
			))
			.toList();
	}

	private List<AdminDashboardResponse.WeeklyTrendPoint> buildWeeklyTrend(
		LocalDate today,
		DateTimeRange todayRange
	) {
		return java.util.stream.IntStream.rangeClosed(0, 6)
			.mapToObj(offset -> today.minusDays(6L - offset))
			.map(date -> {
				DateTimeRange range = date.equals(today) ? todayRange : DateTimeRange.fullDay(date);
				return new AdminDashboardResponse.WeeklyTrendPoint(
					date,
					countUsers(range),
					countCsInquiries(range),
					countDiagnoses(range),
					countAdvertisementEvents(AdvertisementEventType.CLICK, range)
				);
			})
			.toList();
	}

	private AdminDashboardResponse.Checkpoints buildCheckpoints(LocalDate today, LocalDateTime now) {
		long delayedCsCount = inquiryRepository.countByStatusInAndLastMessageAtBefore(
			URGENT_CS_STATUSES,
			now.minusHours(DELAYED_CS_THRESHOLD_HOURS)
		);
		long adsEndingSoonCount = advertisementRepository.findByStatusAndEndAtBetween(
				AdvertisementStatus.ACTIVE,
				now,
				now.plusDays(ADS_ENDING_SOON_DAYS)
			).stream()
			.filter(advertisement -> advertisement.isExposedAt(now))
			.count();

		List<String> todayNoticeIds = noticeRepository.findNoticeIdsByAnnounceDate(today);
		Set<String> hiddenTodayNoticeIds = loadHiddenNoticeIds(todayNoticeIds);

		List<NoticeDocument> noticeLinkCandidates = noticeRepository.findNoticeLinkCandidatesByAnnounceDateLessThanEqual(today);
		Set<String> hiddenCandidateIds = loadHiddenNoticeIds(
			noticeLinkCandidates.stream()
				.map(NoticeDocument::getId)
				.toList()
		);

		long noticesMissingActualLinkCount = noticeLinkCandidates.stream()
			.filter(notice -> !hiddenCandidateIds.contains(notice.getId()))
			.filter(this::isMissingActualLink)
			.count();

		return new AdminDashboardResponse.Checkpoints(
			delayedCsCount,
			adsEndingSoonCount,
			todayNoticeIds.size() - hiddenTodayNoticeIds.size(),
			noticesMissingActualLinkCount
		);
	}

	private Map<UUID, String> loadAdminNames(List<CsInquiry> inquiries) {
		Set<UUID> adminIds = inquiries.stream()
			.map(CsInquiry::getAssignedAdminId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		if (adminIds.isEmpty()) {
			return Map.of();
		}

		return userRepository.findAllById(adminIds).stream()
			.collect(Collectors.toMap(User::getId, User::getName));
	}

	private Set<String> loadHiddenNoticeIds(Collection<String> noticeIds) {
		if (noticeIds == null || noticeIds.isEmpty()) {
			return Set.of();
		}

		return overrideRepository.findByNoticeIdIn(noticeIds).stream()
			.filter(override -> override.isHidden())
			.map(override -> override.getNoticeId())
			.collect(Collectors.toSet());
	}

	private long countUsers(DateTimeRange range) {
		return userRepository.countByCreatedAtBetween(range.start(), range.end());
	}

	private long countCsInquiries(DateTimeRange range) {
		return inquiryRepository.countByCreatedAtBetween(range.start(), range.end());
	}

	private long countDiagnoses(DateTimeRange range) {
		return diagnosisRepository.countByCreatedAtBetween(range.start(), range.end());
	}

	private long countAdvertisementEvents(AdvertisementEventType eventType, DateTimeRange range) {
		return advertisementEventRepository.countByEventTypeAndOccurredAtBetween(eventType, range.start(), range.end());
	}

	private BigDecimal calculateRate(long numerator, long denominator) {
		if (denominator <= 0) {
			return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
		}

		return BigDecimal.valueOf(numerator)
			.multiply(BigDecimal.valueOf(100))
			.divide(BigDecimal.valueOf(denominator), 1, RoundingMode.HALF_UP);
	}

	private BigDecimal calculateChangePercent(long current, long previous) {
		if (previous <= 0) {
			return current > 0
				? BigDecimal.valueOf(100).setScale(1, RoundingMode.HALF_UP)
				: BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
		}

		return BigDecimal.valueOf(current - previous)
			.multiply(BigDecimal.valueOf(100))
			.divide(BigDecimal.valueOf(previous), 1, RoundingMode.HALF_UP);
	}

	private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
		return value != null ? value.atZone(KST).toOffsetDateTime() : null;
	}

	private boolean isMissingActualLink(NoticeDocument notice) {
		Urls urls = notice.getUrls();
		if (urls == null) {
			return true;
		}

		return !hasText(urls.getApply()) && !hasText(urls.getMyhomePc()) && !hasText(urls.getMyhomeMo());
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private String maskName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		if (name.length() == 1) {
			return "*";
		}
		if (name.length() == 2) {
			return name.charAt(0) + "*";
		}
		return name.charAt(0) + "*" + name.charAt(name.length() - 1);
	}

	private record DateTimeRange(LocalDateTime start, LocalDateTime end) {

		private static DateTimeRange of(LocalDateTime start, LocalDateTime end) {
			return new DateTimeRange(start, end);
		}

		private static DateTimeRange fullDay(LocalDate date) {
			LocalDateTime start = date.atStartOfDay();
			return new DateTimeRange(start, date.plusDays(1).atStartOfDay().minusNanos(1));
		}

		private DateTimeRange shiftDays(long days) {
			return new DateTimeRange(start.plusDays(days), end.plusDays(days));
		}

		private DateTimeRange shiftWeeks(long weeks) {
			return new DateTimeRange(start.plusWeeks(weeks), end.plusWeeks(weeks));
		}

		private DateTimeRange shiftMonths(long months) {
			Duration duration = Duration.between(start, end);
			LocalDateTime shiftedStart = start.plusMonths(months);
			return new DateTimeRange(shiftedStart, shiftedStart.plus(duration));
		}
	}
}
