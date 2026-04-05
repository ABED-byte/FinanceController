package com.finance.tracker.repository;

import com.finance.tracker.dto.query.CategorySpendQueryResult;
import com.finance.tracker.entity.Transaction;
import com.finance.tracker.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("""
			SELECT t FROM Transaction t
			WHERE t.user.id = :userId AND t.deleted = false
			AND (:type IS NULL OR t.type = :type)
			AND (:categoryId IS NULL OR t.category.id = :categoryId)
			AND (:startDate IS NULL OR t.date >= :startDate)
			AND (:endDate IS NULL OR t.date <= :endDate)
			ORDER BY t.date DESC, t.id DESC
			""")
	List<Transaction> findFiltered(
			@Param("userId") Long userId,
			@Param("type") TransactionType type,
			@Param("categoryId") Long categoryId,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
			SELECT t FROM Transaction t
			WHERE t.id = :id AND t.user.id = :userId AND t.deleted = false
			""")
	Optional<Transaction> findActiveByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

	@Query("""
			SELECT COALESCE(SUM(t.amount), 0)
			FROM Transaction t
			WHERE t.user.id = :userId AND t.deleted = false AND t.type = :type
			""")
	BigDecimal sumAmountByUserAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

	@Query("""
			SELECT new com.finance.tracker.dto.query.CategorySpendQueryResult(
				c.id,
				c.name,
				c.budgetLimit,
				COALESCE(SUM(CASE
					WHEN t IS NOT NULL
						AND t.deleted = false
						AND t.user.id = :userId
						AND t.type = com.finance.tracker.enums.TransactionType.EXPENSE
						AND t.date >= :start
						AND t.date <= :end
					THEN t.amount
					ELSE 0
				END), 0)
			)
			FROM Category c
			LEFT JOIN c.transactions t
			WHERE c.user.id = :userId
			GROUP BY c.id, c.name, c.budgetLimit
			ORDER BY c.name
			""")
	List<CategorySpendQueryResult> sumExpensesByCategoryForMonth(
			@Param("userId") Long userId,
			@Param("start") LocalDate start,
			@Param("end") LocalDate end);

	@Query("""
			SELECT t FROM Transaction t
			WHERE t.user.id = :userId AND t.deleted = false
			ORDER BY t.date DESC, t.id DESC
			""")
	List<Transaction> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			SELECT year(t.date), month(t.date), t.type, SUM(t.amount)
			FROM Transaction t
			WHERE t.user.id = :userId AND t.deleted = false AND t.date >= :fromDate
			GROUP BY year(t.date), month(t.date), t.type
			ORDER BY year(t.date), month(t.date)
			""")
	List<Object[]> aggregateIncomeExpenseByMonth(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);
}
