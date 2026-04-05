package com.finance.tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalRequest {

	@NotNull
	@DecimalMin(value = "0.0001", inclusive = true, message = "Target amount must be positive")
	private BigDecimal targetAmount;

	@NotBlank
	@Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "targetMonth must be YYYY-MM")
	private String targetMonth;
}
