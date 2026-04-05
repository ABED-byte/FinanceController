package com.finance.tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalProgressResponse {

	private BigDecimal target;
	private BigDecimal saved;
	private int percentage;
}
