package com.finance.tracker.service;

import com.finance.tracker.dto.request.CategoryBudgetUpdateRequest;
import com.finance.tracker.dto.request.CategoryRequest;
import com.finance.tracker.dto.response.CategoryResponse;
import com.finance.tracker.entity.Category;
import com.finance.tracker.entity.User;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final AuthUserAccessor authUserAccessor;

	@Transactional
	public CategoryResponse create(CategoryRequest request) {
		User user = authUserAccessor.requireCurrentUser();
		Category category = Category.builder()
				.name(request.getName())
				.budgetLimit(request.getBudgetLimit())
				.user(user)
				.build();
		categoryRepository.save(category);
		return toResponse(category);
	}

	@Transactional(readOnly = true)
	public List<CategoryResponse> listForCurrentUser() {
		User user = authUserAccessor.requireCurrentUser();
		return categoryRepository.findByUserIdOrderByNameAsc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public CategoryResponse updateBudget(Long id, CategoryBudgetUpdateRequest request) {
		User user = authUserAccessor.requireCurrentUser();
		Category category = categoryRepository.findByIdAndUserId(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		category.setBudgetLimit(request.getBudgetLimit());
		return toResponse(category);
	}

	private CategoryResponse toResponse(Category c) {
		return CategoryResponse.builder()
				.id(c.getId())
				.name(c.getName())
				.budgetLimit(c.getBudgetLimit())
				.build();
	}
}
