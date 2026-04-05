package com.finance.tracker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;
	private T data;
	private String error;
	private String message;
	private Map<String, String> fields;

	public static <T> ApiResponse<T> ok(T data) {
		return ApiResponse.<T>builder().success(true).data(data).build();
	}

	public static <T> ApiResponse<T> fail(String error) {
		return ApiResponse.<T>builder().success(false).error(error).build();
	}

	public static <T> ApiResponse<T> fail(String error, String message) {
		return ApiResponse.<T>builder().success(false).error(error).message(message).build();
	}

	public static <T> ApiResponse<T> validationFail(Map<String, String> fields) {
		return ApiResponse.<T>builder()
				.success(false)
				.error("Validation failed")
				.fields(fields)
				.build();
	}
}
