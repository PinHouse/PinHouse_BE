package co.kr.pinhouse.domain.admin.dashboard.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.domain.admin.dashboard.application.dto.response.AdminDashboardResponse;

public interface AdminDashboardUseCase {

	AdminDashboardResponse getDashboard(UUID adminId);
}
