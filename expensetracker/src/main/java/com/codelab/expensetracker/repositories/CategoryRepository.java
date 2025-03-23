package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

public interface CategoryRepository  extends JpaRepository<Category, String> {


    @Query("from Category c where c.user.userId = :id")
    Page<Category> findCategoriesByUser(@Param("id") int id, Pageable pageable);
    
    
}
