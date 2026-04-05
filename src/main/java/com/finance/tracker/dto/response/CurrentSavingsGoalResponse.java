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
public class CurrentSavingsGoalResponse {

	private Long id;
	private BigDecimal targetAmount;
	private String targetMonth;
	private BigDecimal saved;
	private int percentage;
}
