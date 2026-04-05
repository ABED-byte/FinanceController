package com.finance.tracker.dto.request;

import com.finance.tracker.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	@NotNull
	@DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be positive")
	private BigDecimal amount;

	@NotNull
	private TransactionType type;

	@NotNull
	private Long categoryId;

	@NotNull
	@PastOrPresent(message = "Date must not be in the future")
	private LocalDate date;

	private String notes;
}
