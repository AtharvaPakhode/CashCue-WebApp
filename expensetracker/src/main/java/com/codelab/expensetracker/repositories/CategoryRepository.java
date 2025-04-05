package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, String> {


    @Query("from Category c where c.user.userId = :id")
    Page<Category> findCategoriesByUser (@Param("id") int id, Pageable pageable);

    // query will return a list of Category objects.
    @Query("from Category c where c.user.userId = :id")
    List<Category> ListOfCategoryByUser(@Param("id") int id);

    @Query("FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.user.userId = :id")
    Category findByCategoryNameContainingIgnoreCase(@Param("name")String name ,@Param("id") int id);
        
    



}
