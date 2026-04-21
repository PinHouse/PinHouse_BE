package co.kr.pinhouse.domain.admin.application.dto.response;

import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.domain.user.domain.entity.User;
import lombok.Builder;

@Builder
public record AdminMeResponse(
	UUID adminId,
	String name,
	String email,
	String role,
	List<String> permissions
) {

	public static AdminMeResponse from(User user) {
		return AdminMeResponse.builder()
			.adminId(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.role(user.getRole().name())
			.permissions(List.of("NOTICE_MANAGE", "USER_READ", "CS_HANDLE", "AD_MANAGE", "AUDIT_READ"))
			.build();
	}
}
