package com.finance.tracker.config;

import com.finance.tracker.entity.Category;
import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.entity.SavingsGoal;
import com.finance.tracker.enums.Role;
import com.finance.tracker.enums.TransactionType;
import com.finance.tracker.enums.UserStatus;
import com.finance.tracker.repository.CategoryRepository;
import com.finance.tracker.repository.SavingsGoalRepository;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;
	private final SavingsGoalRepository savingsGoalRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {
		if (userRepository.count() > 0) {
			return;
		}

		saveUser("Admin User", "admin@finance.com", "admin123", Role.ADMIN);
		saveUser("Analyst User", "analyst@finance.com", "analyst123", Role.ANALYST);
		saveUser("Viewer User", "viewer@finance.com", "viewer123", Role.VIEWER);

		User analyst = userRepository.findByEmail("analyst@finance.com").orElseThrow();

		List<Category> categories = new ArrayList<>();
		categories.add(saveCategory(analyst, "Food", new BigDecimal("5000")));
		categories.add(saveCategory(analyst, "Shopping", new BigDecimal("3000")));
		categories.add(saveCategory(analyst, "Transport", new BigDecimal("2000")));
		categories.add(saveCategory(analyst, "Bills", new BigDecimal("8000")));
		categories.add(saveCategory(analyst, "Entertainment", new BigDecimal("1500")));

		LocalDate today = LocalDate.now();
		YearMonth nowYm = YearMonth.from(today);
		YearMonth prev = nowYm.minusMonths(1);
		YearMonth twoAgo = nowYm.minusMonths(2);

		List<Transaction> txs = List.of(
				tx(analyst, categories.get(0), new BigDecimal("1200"), TransactionType.EXPENSE, prev.atDay(5), "Groceries"),
				tx(analyst, categories.get(0), new BigDecimal("800"), TransactionType.EXPENSE, today.minusDays(2), "Dining"),
				tx(analyst, categories.get(1), new BigDecimal("2100"), TransactionType.EXPENSE, prev.atDay(12), "Clothes"),
				tx(analyst, categories.get(1), new BigDecimal("1500"), TransactionType.EXPENSE, today.minusDays(1), "Electronics"),
				tx(analyst, categories.get(2), new BigDecimal("400"), TransactionType.EXPENSE, twoAgo.atDay(20), "Fuel"),
				tx(analyst, categories.get(2), new BigDecimal("350"), TransactionType.EXPENSE, today.minusDays(3), "Transit pass"),
				tx(analyst, categories.get(3), new BigDecimal("4500"), TransactionType.EXPENSE, today.minusDays(4), "Rent share"),
				tx(analyst, categories.get(4), new BigDecimal("600"), TransactionType.EXPENSE, prev.atDay(28), "Streaming"),
				tx(analyst, categories.get(0), new BigDecimal("50000"), TransactionType.INCOME, today.minusDays(5), "Salary"),
				tx(analyst, categories.get(3), new BigDecimal("500"), TransactionType.INCOME, prev.atDay(15), "Cashback")
		);
		transactionRepository.saveAll(txs);

		savingsGoalRepository.save(SavingsGoal.builder()
				.user(analyst)
				.targetAmount(new BigDecimal("20000"))
				.targetMonth(nowYm.toString())
				.build());
	}

	private User saveUser(String name, String email, String rawPassword, Role role) {
		User u = User.builder()
				.name(name)
				.email(email)
				.password(passwordEncoder.encode(rawPassword))
				.role(role)
				.status(UserStatus.ACTIVE)
				.build();
		return userRepository.save(u);
	}

	private Category saveCategory(User user, String name, BigDecimal budget) {
		Category c = Category.builder()
				.name(name)
				.budgetLimit(budget)
				.user(user)
				.build();
		return categoryRepository.save(c);
	}

	private Transaction tx(User user, Category cat, BigDecimal amount, TransactionType type, LocalDate date, String notes) {
		return Transaction.builder()
				.user(user)
				.category(cat)
				.amount(amount)
				.type(type)
				.date(date)
				.notes(notes)
				.deleted(false)
				.build();
	}
}
