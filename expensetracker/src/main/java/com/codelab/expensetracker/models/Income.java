package com.codelab.expensetracker.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ET_income_table")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int incomeId;

    @Size(min = 1, max = 255, message = "Income source must be between 1 and 255 characters")
    private String source;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private double amount;

    @Transient // Used for binding form input
    @NotNull(message = "Date cannot be blank")
    private LocalDate date;

    private LocalDateTime dateTime;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    @ManyToOne
    private User user;

    // Constructors
    public Income() {
    }

    public Income(int incomeId, String source, double amount, LocalDateTime dateTime, String description, User user) {
        this.incomeId = incomeId;
        this.source = source;
        this.amount = amount;
        this.dateTime = dateTime;
        this.description = description;
        this.user = user;
    }

    // Getters and Setters
    public int getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(int incomeId) {
        this.incomeId = incomeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    // toString Method
    @Override
    public String toString() {
        return "Income{" +
                "incomeId=" + incomeId +
                ", source='" + source + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }
}
