package co.kr.pinhouse.domain.admin.dashboard.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record AdminDashboardResponse(
	OffsetDateTime asOf,
	String timezone,
	StatCards statCards,
	TodayBrief todayBrief,
	List<RecentCsItem> recentCs,
	List<WeeklyTrendPoint> weeklyTrend,
	Checkpoints checkpoints
) {

	public record StatCards(
		DashboardMetric totalUsers,
		DashboardMetric csInquiries,
		DashboardMetric activeAdvertisements,
		DashboardMetric monthlyDiagnoses
	) {
	}

	public record DashboardMetric(
		long value,
		Long deltaValue,
		BigDecimal deltaPercent,
		String comparisonLabel,
		BigDecimal subValue,
		String subLabel
	) {
	}

	public record TodayBrief(
		long urgentCsCount,
		int urgentCsThresholdMinutes,
		long newUsersToday,
		long newUsersDeltaFromYesterday,
		long diagnosesCompletedToday,
		BigDecimal diagnosisCompletionRateToday
	) {
	}

	public record RecentCsItem(
		Long inquiryId,
		String title,
		String requesterMaskedName,
		String status,
		OffsetDateTime createdAt,
		OffsetDateTime lastMessageAt,
		String assignedAdminName
	) {
	}

	public record WeeklyTrendPoint(
		LocalDate date,
		long newUsers,
		long csInquiries,
		long diagnosesCompleted,
		long adClicks
	) {
	}

	public record Checkpoints(
		long delayedCsCount,
		long adsEndingSoonCount,
		long newVisibleNoticesToday,
		long noticesMissingActualLinkCount
	) {
	}
}
