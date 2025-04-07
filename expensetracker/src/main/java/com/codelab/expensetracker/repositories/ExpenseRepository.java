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

    



}
