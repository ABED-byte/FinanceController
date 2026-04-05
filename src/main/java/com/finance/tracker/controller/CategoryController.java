package com.finance.tracker.controller;

import com.finance.tracker.dto.request.CategoryBudgetUpdateRequest;
import com.finance.tracker.dto.request.CategoryRequest;
import com.finance.tracker.dto.response.ApiResponse;
import com.finance.tracker.dto.response.CategoryResponse;
import com.finance.tracker.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(categoryService.create(request)));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> list() {
		return ResponseEntity.ok(ApiResponse.ok(categoryService.listForCurrentUser()));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateBudget(
			@PathVariable Long id,
			@RequestBody CategoryBudgetUpdateRequest request) {
		return ResponseEntity.ok(ApiResponse.ok(categoryService.updateBudget(id, request)));
	}
}
