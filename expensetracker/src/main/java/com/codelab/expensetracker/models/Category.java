package com.codelab.expensetracker.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import javax.validation.constraints.*;

@Entity
@Table(name="ET_category_table")
public class Category {

    @Id
    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    private String categoryName;

    @NotNull(message = "Monthly budget cannot be null")
    @DecimalMin(value = "0.01", message = "Monthly budget must be greater than 0")
    private double categoryMonthlyBudget;

    @ManyToOne
    private User user; // Reference to the User entity (foreign key relationship)

    // Constructors
    public Category() {
    }

    public Category(String categoryName, double categoryMonthlyBudget, User user) {
        this.categoryName = categoryName;
        this.categoryMonthlyBudget = categoryMonthlyBudget;
        this.user = user;
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

    // toString Method
    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName + '\'' +
                ", categoryMonthlyBudget=" + categoryMonthlyBudget +
                ", user=" + user +
                '}';
    }
}
