package com.finance.tracker.service;

import com.finance.tracker.dto.request.TransactionRequest;
import com.finance.tracker.dto.response.TransactionResponse;
import com.finance.tracker.entity.Category;
import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.enums.TransactionType;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.repository.CategoryRepository;
import com.finance.tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final CategoryRepository categoryRepository;
	public final AuthUserAccessor authUserAccessor;

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "dashboard_summary", key = "#root.target.authUserAccessor.requireCurrentUser().id"),
			@CacheEvict(value = "dashboard_trends", key = "#root.target.authUserAccessor.requireCurrentUser().id")
	})
	public TransactionResponse create(TransactionRequest request) {
		User user = authUserAccessor.requireCurrentUser();
		Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to you"));
		Transaction tx = Transaction.builder()
				.amount(request.getAmount())
				.type(request.getType())
				.category(category)
				.date(request.getDate())
				.notes(request.getNotes())
				.user(user)
				.deleted(false)
				.build();
		transactionRepository.save(tx);
		return toResponse(tx);
	}

	@Transactional(readOnly = true)
	public List<TransactionResponse> list(
			TransactionType type,
			Long categoryId,
			LocalDate startDate,
			LocalDate endDate) {
		User user = authUserAccessor.requireCurrentUser();
		return transactionRepository
				.findFiltered(user.getId(), type, categoryId, startDate, endDate)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public TransactionResponse getById(Long id) {
		User user = authUserAccessor.requireCurrentUser();
		Transaction tx = transactionRepository.findActiveByIdAndUserId(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		return toResponse(tx);
	}

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "dashboard_summary", key = "#root.target.authUserAccessor.requireCurrentUser().id"),
			@CacheEvict(value = "dashboard_trends", key = "#root.target.authUserAccessor.requireCurrentUser().id")
	})
	public TransactionResponse update(Long id, TransactionRequest request) {
		User user = authUserAccessor.requireCurrentUser();
		Transaction tx = transactionRepository.findActiveByIdAndUserId(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to you"));
		tx.setAmount(request.getAmount());
		tx.setType(request.getType());
		tx.setCategory(category);
		tx.setDate(request.getDate());
		tx.setNotes(request.getNotes());
		return toResponse(tx);
	}

	@Transactional
	@Caching(evict = {
			@CacheEvict(value = "dashboard_summary", key = "#root.target.authUserAccessor.requireCurrentUser().id"),
			@CacheEvict(value = "dashboard_trends", key = "#root.target.authUserAccessor.requireCurrentUser().id")
	})
	public void softDelete(Long id) {
		User user = authUserAccessor.requireCurrentUser();
		Transaction tx = transactionRepository.findActiveByIdAndUserId(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		tx.setDeleted(true);
	}

	private TransactionResponse toResponse(Transaction tx) {
		return TransactionResponse.builder()
				.id(tx.getId())
				.amount(tx.getAmount())
				.type(tx.getType())
				.categoryId(tx.getCategory().getId())
				.categoryName(tx.getCategory().getName())
				.date(tx.getDate())
				.notes(tx.getNotes())
				.createdAt(tx.getCreatedAt())
				.build();
	}
}
