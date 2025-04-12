package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {




    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByUser(@Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.dateTime DESC")
    Page<Expense> findTransactionsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT SUM(e.amount)  From Expense e WHERE e.user = :user ")
    double findSumOfExpensesOfUserByUserId(@Param("user") User user);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.dateTime BETWEEN :startOfMonth AND :endOfMonth")
    Double findSumOfExpensesForCurrentMonth(@Param("user") User user,
                                            @Param("startOfMonth") LocalDateTime startOfMonth,
                                            @Param("endOfMonth") LocalDateTime endOfMonth);


    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.dateTime BETWEEN :startOfQuarter AND :endOfQuarter")
    Double findSumOfExpenseForQuarter(@Param("user") User user,
                                     @Param("startOfQuarter") LocalDateTime startOfQuarter,
                                     @Param("endOfQuarter") LocalDateTime endOfQuarter);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.dateTime BETWEEN :startOfYear AND :endOfYear")
    Double findSumOfExpenseForYear(@Param("user") User user,
                                  @Param("startOfYear") LocalDateTime startOfYear,
                                  @Param("endOfYear") LocalDateTime endOfYear);



    @Query("SELECT EXTRACT(MONTH FROM e.dateTime) AS month, COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY EXTRACT(MONTH FROM e.dateTime) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyExpenseSumsWithMonth(@Param("user") User user);

    @Query("SELECT " +
            "  CASE " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 1 AND 3 THEN 1 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 4 AND 6 THEN 2 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 7 AND 9 THEN 3 " +
            "    ELSE 4 " +
            "  END AS quarter, " +
            "  COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY quarter " +
            "ORDER BY quarter ASC")
    List<Object[]> getQuarterlyExpenseSumsWithQuarter(@Param("user") User user);

    @Query("SELECT EXTRACT(YEAR FROM e.dateTime) AS year, COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "GROUP BY EXTRACT(YEAR FROM e.dateTime) " +
            "ORDER BY year ASC")
    List<Object[]> getYearlyExpenseSumsWithYear(@Param("user") User user);


}
