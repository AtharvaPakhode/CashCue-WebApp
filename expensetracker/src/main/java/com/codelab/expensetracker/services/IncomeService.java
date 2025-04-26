package com.codelab.expensetracker.services;


import com.codelab.expensetracker.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {

     @Autowired
    private IncomeRepository incomeRepository;

    // Method to delete Income by Id
    public boolean deleteIncomeById(int id) {
        // Check if the Income exists
        if (incomeRepository.existsById(id)) {

            incomeRepository.deleteById(id);
            
            return true;

        }
        return false;  // Income not found
    }
}
