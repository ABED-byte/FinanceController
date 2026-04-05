package com.finance.tracker.dto.response;

import com.finance.tracker.enums.Role;
import com.finance.tracker.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {

	private Long id;
	private String name;
	private String email;
	private Role role;
	private UserStatus status;
	private LocalDateTime createdAt;
}
