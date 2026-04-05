package com.finance.tracker.dto.request;

import com.finance.tracker.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank
	private String name;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotNull
	private Role role;
}
