package com.codelab.expensetracker.services;


import com.codelab.expensetracker.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Method to delete Expense by Id
    public boolean deleteIncomeById(int id) {
        // Check if the Expense exists
        if (expenseRepository.existsById(id)) {

            expenseRepository.deleteById(id);

            return true;

        }
        return false;  // Expense not found
    }
}
