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

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> , JpaSpecificationExecutor<Income> {

    @Query("SELECT i FROM Income i WHERE i.user = :user ORDER BY i.dateTime DESC")
    List<Income> getIncomeByUser(@Param("user") User user);

    @Query("SELECT i FROM Income i WHERE i.user = :user ORDER BY i.dateTime DESC")
    Page<Income> findTransactionsByUser(@Param("user") User user, Pageable pageable);


}

