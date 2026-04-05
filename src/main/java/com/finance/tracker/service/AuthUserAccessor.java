package com.finance.tracker.service;

import com.finance.tracker.entity.User;
import com.finance.tracker.exception.UnauthorizedException;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserAccessor {

	private final UserRepository userRepository;

	public User requireCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
			throw new UnauthorizedException("Not authenticated");
		}
		return userRepository.findByEmail(principal.getUsername())
				.orElseThrow(() -> new UnauthorizedException("User not found"));
	}
}
