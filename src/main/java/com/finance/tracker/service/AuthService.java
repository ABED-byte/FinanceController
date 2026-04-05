package com.finance.tracker.service;

import com.finance.tracker.dto.request.LoginRequest;
import com.finance.tracker.dto.request.RegisterRequest;
import com.finance.tracker.dto.response.AuthResponse;
import com.finance.tracker.entity.User;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.security.JwtUtil;
import com.finance.tracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered");
		}
		User user = User.builder()
				.name(request.getName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		User saved = userRepository.save(user);
		UserPrincipal principal = new UserPrincipal(saved);
		String token = jwtUtil.generateToken(principal);
		return AuthResponse.builder()
				.token(token)
				.role(saved.getRole())
				.name(saved.getName())
				.build();
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		UserPrincipal principal = new UserPrincipal(user);
		String token = jwtUtil.generateToken(principal);
		return AuthResponse.builder()
				.token(token)
				.role(user.getRole())
				.name(user.getName())
				.build();
	}
}
