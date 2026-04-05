package com.finance.tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

	private BigDecimal totalIncome;
	private BigDecimal totalExpenses;
	private BigDecimal netBalance;
	private SavingsGoalProgressResponse savingsGoalProgress;
	private List<CategoryBreakdownResponse> categoryBreakdown;
	private List<TransactionResponse> recentTransactions;
}
