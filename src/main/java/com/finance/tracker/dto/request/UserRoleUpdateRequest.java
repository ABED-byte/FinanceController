package com.finance.tracker.dto.request;

import com.finance.tracker.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Set user role (use VIEWER to revoke analyst/admin access)")
public class UserRoleUpdateRequest {

	@NotNull
	@Schema(
			description = "VIEWER, ANALYST, or ADMIN — promote via POST .../promote for ANALYST/ADMIN only",
			example = "ANALYST",
			allowableValues = {"VIEWER", "ANALYST", "ADMIN"})
	private Role role;
}
