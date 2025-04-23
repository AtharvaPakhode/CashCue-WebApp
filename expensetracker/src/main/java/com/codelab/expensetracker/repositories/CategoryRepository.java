package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, String> {


    @Query("from Category c where c.user.userId = :id")
    Page<Category> findCategoriesByUser (@Param("id") int id, Pageable pageable);

    @Query("SELECT c.categoryID FROM Category c WHERE c.categoryName = :name AND c.user.userId = :id")
    Integer findByCategoryIDByNameAndUser(@Param("name") String name, @Param("id") int id);


    // query will return a list of Category objects.
    @Query("from Category c where c.user.userId = :id")
    List<Category> ListOfCategoryByUser(@Param("id") int id);

    @Query("FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.user.userId = :id")
    Category findByCategoryNameContainingIgnoreCase(@Param("name")String name ,@Param("id") int id);

    @Query("SELECT c FROM Category c WHERE c.categoryName IN :names AND c.user.userId = :userId")
    List<Category> findByCategoryNameInAndUserId(@Param("names") List<String> names, @Param("userId") int userId);


    
    // reports

    //for current month
    @Query( "SELECT c.categoryName, SUM(e.amount) " +
            "FROM Expense e " +
            "JOIN Category c ON e.category.categoryName = c.categoryName " +
            "WHERE e.user = :user " +
            "AND c.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM e.dateTime) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "GROUP BY c.categoryName "+
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCurrentMonthCategorySums(@Param("user") User user);

    
    //for past month
    @Query(value = "SELECT c.categoryName, SUM(e.amount) " +
            "FROM Expense e " +
            "JOIN Category c ON e.category.categoryName = c.categoryName " +
            "WHERE e.user = :user " +
            "AND c.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM e.dateTime) = EXTRACT(MONTH FROM CURRENT_DATE) - 1 " +
            "GROUP BY c.categoryName " +
            "ORDER BY SUM(e.amount) DESC", nativeQuery = false)
    List<Object[]> getPastMonthCategorySums(@Param("user") User user);




    //for current quarter
    @Query("SELECT " +
            "  CASE " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 1 AND 3 THEN 1 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 4 AND 6 THEN 2 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 7 AND 9 THEN 3 " +
            "    ELSE 4 " +
            "  END AS quarter, " +
            "  e.category.categoryName, " +  // Reference the categoryName field
            "  SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY quarter, e.category.categoryName " +
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCurrentQuarterCategorySums(@Param("user") User user);


    //for past quarter
    @Query("SELECT " +
            "  CASE " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 1 AND 3 THEN 1 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 4 AND 6 THEN 2 " +
            "    WHEN EXTRACT(MONTH FROM e.dateTime) BETWEEN 7 AND 9 THEN 3 " +
            "    ELSE 4 " +
            "  END AS quarter, " +
            "  e.category.categoryName, " +  // Reference the categoryName field
            "  SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND (EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "     AND EXTRACT(QUARTER FROM e.dateTime) = EXTRACT(QUARTER FROM CURRENT_DATE) - 1) " +
            "OR (EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) - 1 " +
            "     AND EXTRACT(QUARTER FROM e.dateTime) = 4) " +
            "GROUP BY quarter, e.category.categoryName " +
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getPastQuarterCategorySums(@Param("user") User user);
    
    
    

   //for current year
    @Query("SELECT e.category.categoryName, SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY e.category.categoryName "+
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCurrentYearCategorySums(@Param("user") User user);


    
    //for past year
    @Query("SELECT e.category.categoryName, SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.user = :user " +
            "AND EXTRACT(YEAR FROM e.dateTime) = EXTRACT(YEAR FROM CURRENT_DATE)-1 " +
            "GROUP BY e.category.categoryName "+
            "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getPastYearCategorySums(@Param("user") User user);
    


}
