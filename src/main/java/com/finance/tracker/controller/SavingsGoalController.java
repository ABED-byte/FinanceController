package com.finance.tracker.controller;

import com.finance.tracker.dto.request.SavingsGoalRequest;
import com.finance.tracker.dto.response.ApiResponse;
import com.finance.tracker.dto.response.CurrentSavingsGoalResponse;
import com.finance.tracker.dto.response.SavingsGoalResponse;
import com.finance.tracker.service.SavingsGoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Savings goals")
public class SavingsGoalController {

	private final SavingsGoalService savingsGoalService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<SavingsGoalResponse>> create(@Valid @RequestBody SavingsGoalRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(savingsGoalService.create(request)));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<List<SavingsGoalResponse>>> list() {
		return ResponseEntity.ok(ApiResponse.ok(savingsGoalService.list()));
	}

	@GetMapping("/current")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<CurrentSavingsGoalResponse>> current() {
		return ResponseEntity.ok(ApiResponse.ok(savingsGoalService.getCurrentMonthProgress()));
	}
}
