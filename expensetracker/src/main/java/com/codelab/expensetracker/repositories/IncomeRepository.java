package com.codelab.expensetracker.repositories;


import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.Income;
import com.codelab.expensetracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> , JpaSpecificationExecutor<Income> {

    @Query("SELECT i FROM Income i WHERE i.user = :user ORDER BY i.dateTime DESC")
    List<Income> getIncomeByUser(@Param("user") User user);

    @Query("SELECT i FROM Income i WHERE i.user = :user ORDER BY i.dateTime DESC")
    Page<Income> findTransactionsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT SUM(i.amount)  From Income i WHERE i.user = :user ")
    double findSumOfExpensesOfUserByUserId(@Param("user") User user);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.dateTime BETWEEN :startOfMonth AND :endOfMonth")
    Double findSumOfIncomeForCurrentMonth(@Param("user") User user,
                                          @Param("startOfMonth") LocalDateTime startOfMonth,
                                          @Param("endOfMonth") LocalDateTime endOfMonth);


    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.dateTime BETWEEN :startOfQuarter AND :endOfQuarter")
    Double findSumOfIncomeForQuarter(@Param("user") User user,
                                     @Param("startOfQuarter") LocalDateTime startOfQuarter,
                                     @Param("endOfQuarter") LocalDateTime endOfQuarter);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.dateTime BETWEEN :startOfYear AND :endOfYear")
    Double findSumOfIncomeForYear(@Param("user") User user,
                                  @Param("startOfYear") LocalDateTime startOfYear,
                                  @Param("endOfYear") LocalDateTime endOfYear);



    @Query("SELECT EXTRACT(MONTH FROM i.dateTime) AS month, COALESCE(SUM(i.amount), 0) " +
            "FROM Income i " +
            "WHERE i.user = :user " +
            "AND EXTRACT(YEAR FROM i.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY EXTRACT(MONTH FROM i.dateTime) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyIncomeSumsWithMonth(@Param("user") User user);

    @Query("SELECT " +
            "  CASE " +
            "    WHEN EXTRACT(MONTH FROM i.dateTime) BETWEEN 1 AND 3 THEN 1 " +
            "    WHEN EXTRACT(MONTH FROM i.dateTime) BETWEEN 4 AND 6 THEN 2 " +
            "    WHEN EXTRACT(MONTH FROM i.dateTime) BETWEEN 7 AND 9 THEN 3 " +
            "    ELSE 4 " +
            "  END AS quarter, " +
            "  COALESCE(SUM(i.amount), 0) " +
            "FROM Income i " +
            "WHERE i.user = :user " +
            "AND EXTRACT(YEAR FROM i.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY quarter " +
            "ORDER BY quarter ASC")
    List<Object[]> getQuarterlyIncomeSumsWithQuarter(@Param("user") User user);

    @Query("SELECT EXTRACT(YEAR FROM i.dateTime) AS year, COALESCE(SUM(i.amount), 0) " +
            "FROM Income i " +
            "WHERE i.user = :user " +
            "GROUP BY EXTRACT(YEAR FROM i.dateTime) " +
            "ORDER BY year ASC")
    List<Object[]> getYearlyIncomeSumsWithYear(@Param("user") User user);





}

