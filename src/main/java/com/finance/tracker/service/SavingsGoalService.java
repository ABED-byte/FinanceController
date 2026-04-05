package com.finance.tracker.service;

import com.finance.tracker.dto.request.SavingsGoalRequest;
import com.finance.tracker.dto.response.CurrentSavingsGoalResponse;
import com.finance.tracker.dto.response.SavingsGoalResponse;
import com.finance.tracker.entity.SavingsGoal;
import com.finance.tracker.entity.User;
import com.finance.tracker.enums.TransactionType;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.repository.SavingsGoalRepository;
import com.finance.tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

	private final SavingsGoalRepository savingsGoalRepository;
	private final TransactionRepository transactionRepository;
	private final AuthUserAccessor authUserAccessor;

	@Transactional
	public SavingsGoalResponse create(SavingsGoalRequest request) {
		User user = authUserAccessor.requireCurrentUser();
		SavingsGoal goal = SavingsGoal.builder()
				.targetAmount(request.getTargetAmount())
				.targetMonth(request.getTargetMonth())
				.user(user)
				.build();
		savingsGoalRepository.save(goal);
		return toResponse(goal);
	}

	@Transactional(readOnly = true)
	public List<SavingsGoalResponse> list() {
		User user = authUserAccessor.requireCurrentUser();
		return savingsGoalRepository.findByUserIdOrderByTargetMonthDesc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public CurrentSavingsGoalResponse getCurrentMonthProgress() {
		User user = authUserAccessor.requireCurrentUser();
		String ym = YearMonth.now().toString();
		SavingsGoal goal = savingsGoalRepository.findByUserIdAndTargetMonth(user.getId(), ym)
				.orElseThrow(() -> new ResourceNotFoundException("No savings goal for the current month"));
		BigDecimal income = transactionRepository.sumAmountByUserAndType(user.getId(), TransactionType.INCOME);
		BigDecimal expenses = transactionRepository.sumAmountByUserAndType(user.getId(), TransactionType.EXPENSE);
		BigDecimal saved = income.subtract(expenses);
		int percentage = computeProgressPercentage(saved, goal.getTargetAmount());
		return CurrentSavingsGoalResponse.builder()
				.id(goal.getId())
				.targetAmount(goal.getTargetAmount())
				.targetMonth(goal.getTargetMonth())
				.saved(saved)
				.percentage(percentage)
				.build();
	}

	private int computeProgressPercentage(BigDecimal saved, BigDecimal target) {
		if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) {
			return 0;
		}
		return saved.multiply(BigDecimal.valueOf(100))
				.divide(target, 0, RoundingMode.HALF_UP)
				.intValue();
	}

	private SavingsGoalResponse toResponse(SavingsGoal g) {
		return SavingsGoalResponse.builder()
				.id(g.getId())
				.targetAmount(g.getTargetAmount())
				.targetMonth(g.getTargetMonth())
				.build();
	}
}
