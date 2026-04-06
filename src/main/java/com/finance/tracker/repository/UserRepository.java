package com.finance.tracker.repository;

import com.finance.tracker.entity.User;
import com.finance.tracker.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	long countByRole(Role role);
}
