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
public class CategoryBreakdownResponse {

	private String category;
	private BigDecimal spent;
	private BigDecimal budget;
	private int percentage;
	private boolean exceeded;
}
