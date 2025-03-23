package com.codelab.expensetracker.services;

import com.codelab.expensetracker.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Method to delete category by name
    public boolean deleteCategoryByName(String name) {
        // Check if the category exists
        if (categoryRepository.existsById(name)) {
            
            categoryRepository.deleteById(name);
            System.out.println("true");
            return true;
           
        }
        return false;  // Category not found
    }
}
