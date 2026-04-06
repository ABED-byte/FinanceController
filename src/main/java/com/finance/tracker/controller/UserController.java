package com.finance.tracker.controller;

import com.finance.tracker.dto.request.PromoteUserRequest;
import com.finance.tracker.dto.request.UserRoleUpdateRequest;
import com.finance.tracker.dto.request.UserStatusUpdateRequest;
import com.finance.tracker.dto.response.ApiResponse;
import com.finance.tracker.dto.response.UserAdminResponse;
import com.finance.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

	private final UserService userService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<List<UserAdminResponse>>> list() {
		return ResponseEntity.ok(ApiResponse.ok(userService.listAll()));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserAdminResponse>> updateStatus(
			@PathVariable Long id,
			@Valid @RequestBody UserStatusUpdateRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(userService.updateStatus(id, request)));
	}

	@PostMapping("/{id}/promote")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Promote user to analyst or admin", description = "Assigns ANALYST or ADMIN. Use PATCH /{id}/role with VIEWER to revoke elevated access.")
	public ResponseEntity<ApiResponse<UserAdminResponse>> promote(
			@PathVariable Long id,
			@Valid @RequestBody PromoteUserRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(userService.promote(id, request)));
	}

	@PatchMapping("/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Set user role", description = "Set VIEWER, ANALYST, or ADMIN. Prefer POST /{id}/promote when granting ANALYST or ADMIN.")
	public ResponseEntity<ApiResponse<UserAdminResponse>> updateRole(
			@PathVariable Long id,
			@Valid @RequestBody UserRoleUpdateRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(userService.updateRole(id, request)));
	}
}
