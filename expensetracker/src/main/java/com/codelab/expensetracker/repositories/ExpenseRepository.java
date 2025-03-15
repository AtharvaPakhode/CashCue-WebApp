package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

}
