package com.codelab.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="ET_expense_table")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int expenseId;

    
    @Size(min = 1, max = 255, message = "Expense name must be between 1 and 255 characters")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryID")
    private Category category;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @Transient // So Spring doesn't try to save it to DB
    @NotNull(message = "Date cannot be blank")
    private LocalDate date; // for binding the form input

    
    private LocalDateTime dateTime;

    
    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    @ManyToOne
    private User user; // Foreign Key referencing the User table

    // Constructors
    public Expense() {
    }

    public Expense(int expenseId, String name, double amount, Category category, String paymentMethod, LocalDateTime dateTime, String description, User user) {
        this.expenseId = expenseId;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.dateTime = dateTime;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }

    public void setLocalDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public void setUser(User user) {
        this.user = user;
    }

    public  LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
