package com.finance.tracker.controller;

import com.finance.tracker.dto.response.ApiResponse;
import com.finance.tracker.dto.response.DashboardSummaryResponse;
import com.finance.tracker.dto.response.MonthlyTrendResponse;
import com.finance.tracker.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<DashboardSummaryResponse>> summary() {
		return ResponseEntity.ok(ApiResponse.ok(dashboardService.summary()));
	}

	@GetMapping("/trends")
	@PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> trends() {
		return ResponseEntity.ok(ApiResponse.ok(dashboardService.trendsLastSixMonths()));
	}
}
