package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Integer> {
}
