package com.finance.tracker.controller;

import com.finance.tracker.dto.request.TransactionRequest;
import com.finance.tracker.dto.response.ApiResponse;
import com.finance.tracker.dto.response.TransactionResponse;
import com.finance.tracker.enums.TransactionType;
import com.finance.tracker.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<TransactionResponse>> create(@Valid @RequestBody TransactionRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(transactionService.create(request)));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<List<TransactionResponse>>> list(
			@RequestParam(required = false) TransactionType type,
			@RequestParam(required = false) Long category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity.ok(ApiResponse.ok(transactionService.list(type, category, startDate, endDate)));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<TransactionResponse>> getOne(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(transactionService.getById(id)));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<TransactionResponse>> update(
			@PathVariable Long id,
			@Valid @RequestBody TransactionRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(transactionService.update(id, request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		transactionService.softDelete(id);
		return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
	}
}
