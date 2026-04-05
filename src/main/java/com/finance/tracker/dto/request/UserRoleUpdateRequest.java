package com.finance.tracker.dto.request;

import com.finance.tracker.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateRequest {

	@NotNull
	private Role role;
}
