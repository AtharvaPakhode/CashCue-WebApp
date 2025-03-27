package com.codelab.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="ET_expense_table")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int expenseId;

    @NotBlank(message = "Expense name cannot be blank")
    @Size(min = 1, max = 255, message = "Expense name must be between 1 and 255 characters")
    private String name;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Category cannot be blank")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    private String category;

    @NotBlank(message = "Payment method cannot be blank")
    @Size(min = 3, max = 50, message = "Payment method must be between 3 and 50 characters")
    private String paymentMethod;

    
    private LocalDate date;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    @ManyToOne
    private User user; // Foreign Key referencing the User table

    // Constructors
    public Expense() {
    }

    public Expense(int expenseId, String name, double amount, String category, String paymentMethod, LocalDate date, String description, User user) {
        this.expenseId = expenseId;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.description = description;
        this.user = user;
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    // toString Method
    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
