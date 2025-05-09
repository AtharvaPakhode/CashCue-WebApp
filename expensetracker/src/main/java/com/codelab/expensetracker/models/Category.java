package com.codelab.expensetracker.models;

import jakarta.persistence.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name="ET_category_table")
public class Category {

   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int categoryID;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String categoryName;

    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than zero")
    private double categoryMonthlyBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // Constructors
    public Category() {
    }

    public Category(String categoryName, double categoryMonthlyBudget) {
        this.categoryName = categoryName;
        this.categoryMonthlyBudget = categoryMonthlyBudget;
    }

    public Category(int categoryID, User user, double categoryMonthlyBudget, String categoryName) {
        this.categoryID = categoryID;
        this.user = user;
        this.categoryMonthlyBudget = categoryMonthlyBudget;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getCategoryMonthlyBudget() {
        return categoryMonthlyBudget;
    }

    public void setCategoryMonthlyBudget(double categoryMonthlyBudget) {
        this.categoryMonthlyBudget = categoryMonthlyBudget;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
