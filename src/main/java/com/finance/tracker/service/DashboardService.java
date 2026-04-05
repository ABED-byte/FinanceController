package com.finance.tracker.service;

import com.finance.tracker.dto.query.CategorySpendQueryResult;
import com.finance.tracker.dto.response.CategoryBreakdownResponse;
import com.finance.tracker.dto.response.DashboardSummaryResponse;
import com.finance.tracker.dto.response.MonthlyTrendResponse;
import com.finance.tracker.dto.response.SavingsGoalProgressResponse;
import com.finance.tracker.dto.response.TransactionResponse;
import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.enums.TransactionType;
import com.finance.tracker.repository.SavingsGoalRepository;
import com.finance.tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final TransactionRepository transactionRepository;
	private final SavingsGoalRepository savingsGoalRepository;
	private final AuthUserAccessor authUserAccessor;

	@Transactional(readOnly = true)
	public DashboardSummaryResponse summary() {
		User user = authUserAccessor.requireCurrentUser();
		BigDecimal totalIncome = nullToZero(
				transactionRepository.sumAmountByUserAndType(user.getId(), TransactionType.INCOME));
		BigDecimal totalExpenses = nullToZero(
				transactionRepository.sumAmountByUserAndType(user.getId(), TransactionType.EXPENSE));
		BigDecimal netBalance = totalIncome.subtract(totalExpenses);

		YearMonth ym = YearMonth.now();
		LocalDate monthStart = ym.atDay(1);
		LocalDate monthEnd = ym.atEndOfMonth();
		List<CategorySpendQueryResult> spendRows =
				transactionRepository.sumExpensesByCategoryForMonth(user.getId(), monthStart, monthEnd);

		List<CategoryBreakdownResponse> breakdown = spendRows.stream()
				.map(row -> mapCategoryBreakdown(row))
				.toList();

		SavingsGoalProgressResponse goalProgress = savingsGoalRepository
				.findByUserIdAndTargetMonth(user.getId(), ym.toString())
				.map(g -> mapSavingsProgress(netBalance, g.getTargetAmount()))
				.orElse(null);

		List<Transaction> recent = transactionRepository.findRecentByUserId(
				user.getId(), PageRequest.of(0, 5));
		List<TransactionResponse> recentDtos = recent.stream().map(this::toTransactionResponse).toList();

		return DashboardSummaryResponse.builder()
				.totalIncome(totalIncome)
				.totalExpenses(totalExpenses)
				.netBalance(netBalance)
				.savingsGoalProgress(goalProgress)
				.categoryBreakdown(breakdown)
				.recentTransactions(recentDtos)
				.build();
	}

	@Transactional(readOnly = true)
	public List<MonthlyTrendResponse> trendsLastSixMonths() {
		User user = authUserAccessor.requireCurrentUser();
		YearMonth end = YearMonth.now();
		YearMonth start = end.minusMonths(5);
		LocalDate fromDate = start.atDay(1);
		List<Object[]> rows = transactionRepository.aggregateIncomeExpenseByMonth(user.getId(), fromDate);

		Map<String, BigDecimal> incomeByMonth = new HashMap<>();
		Map<String, BigDecimal> expenseByMonth = new HashMap<>();
		for (Object[] row : rows) {
			int y = ((Number) row[0]).intValue();
			int m = ((Number) row[1]).intValue();
			String key = YearMonth.of(y, m).toString();
			TransactionType type = (TransactionType) row[2];
			BigDecimal sum = (BigDecimal) row[3];
			if (type == TransactionType.INCOME) {
				incomeByMonth.merge(key, sum, BigDecimal::add);
			} else {
				expenseByMonth.merge(key, sum, BigDecimal::add);
			}
		}

		List<MonthlyTrendResponse> result = new ArrayList<>();
		for (YearMonth cursor = start; !cursor.isAfter(end); cursor = cursor.plusMonths(1)) {
			String key = cursor.toString();
			result.add(MonthlyTrendResponse.builder()
					.month(key)
					.income(nullToZero(incomeByMonth.get(key)))
					.expenses(nullToZero(expenseByMonth.get(key)))
					.build());
		}
		return result;
	}

	private CategoryBreakdownResponse mapCategoryBreakdown(CategorySpendQueryResult row) {
		BigDecimal spent = nullToZero(row.getSpent());
		BigDecimal budget = row.getBudgetLimit();
		int pct = 0;
		boolean exceeded = false;
		if (budget != null && budget.compareTo(BigDecimal.ZERO) > 0) {
			pct = spent.multiply(BigDecimal.valueOf(100))
					.divide(budget, 0, RoundingMode.HALF_UP)
					.intValue();
			exceeded = spent.compareTo(budget) > 0;
		}
		return CategoryBreakdownResponse.builder()
				.category(row.getName())
				.spent(spent)
				.budget(budget)
				.percentage(pct)
				.exceeded(exceeded)
				.build();
	}

	private SavingsGoalProgressResponse mapSavingsProgress(BigDecimal saved, BigDecimal target) {
		int pct = 0;
		if (target != null && target.compareTo(BigDecimal.ZERO) > 0) {
			pct = saved.multiply(BigDecimal.valueOf(100))
					.divide(target, 0, RoundingMode.HALF_UP)
					.intValue();
		}
		return SavingsGoalProgressResponse.builder()
				.target(target)
				.saved(saved)
				.percentage(pct)
				.build();
	}

	private TransactionResponse toTransactionResponse(Transaction tx) {
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

	private static BigDecimal nullToZero(BigDecimal v) {
		return Optional.ofNullable(v).orElse(BigDecimal.ZERO);
	}
}
