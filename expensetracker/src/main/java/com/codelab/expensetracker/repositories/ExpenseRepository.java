package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {


    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByUser(@Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.dateTime DESC")
    Page<Expense> findTransactionsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.category = :category ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByCategory(@Param("user") User user, @Param("category") String category);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.amount BETWEEN :minAmount AND :maxAmount ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByRangeOfAmount(@Param("user") User user, @Param("minAmount") double minAmount, @Param("maxAmount") double maxAmount);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.paymentMethod = :paymentMethod ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByPaymentMethod(@Param("user") User user, @Param("paymentMethod") String paymentMethod);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.dateTime BETWEEN :startDate AND :endDate ORDER BY e.dateTime DESC")
    List<Expense> getExpenseByRangeOfDate(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    

}
