package com.finance.tracker.dto.request;

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
@Schema(description = "Promote a user to analyst or administrator")
public class PromoteUserRequest {

	public enum ElevatedRole {
		ANALYST,
		ADMIN
	}

	@NotNull
	@Schema(description = "Target elevated role", example = "ANALYST", requiredMode = Schema.RequiredMode.REQUIRED)
	private ElevatedRole role;
}
