package com.finance.tracker.service;

import com.finance.tracker.dto.request.UserRoleUpdateRequest;
import com.finance.tracker.dto.request.UserStatusUpdateRequest;
import com.finance.tracker.dto.response.UserAdminResponse;
import com.finance.tracker.entity.User;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<UserAdminResponse> listAll() {
		return userRepository.findAll().stream().map(this::toAdminResponse).toList();
	}

	@Transactional
	public UserAdminResponse updateStatus(Long id, UserStatusUpdateRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		user.setStatus(request.getStatus());
		return toAdminResponse(user);
	}

	@Transactional
	public UserAdminResponse updateRole(Long id, UserRoleUpdateRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		user.setRole(request.getRole());
		return toAdminResponse(user);
	}

	private UserAdminResponse toAdminResponse(User u) {
		return UserAdminResponse.builder()
				.id(u.getId())
				.name(u.getName())
				.email(u.getEmail())
				.role(u.getRole())
				.status(u.getStatus())
				.createdAt(u.getCreatedAt())
				.build();
	}
}
