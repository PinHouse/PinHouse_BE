package co.kr.pinhouse.domain.admin.user.application.dto.request;

import co.kr.pinhouse.domain.user.domain.entity.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateAdminUserRoleRequest(
	@NotNull Role role
) {
}
