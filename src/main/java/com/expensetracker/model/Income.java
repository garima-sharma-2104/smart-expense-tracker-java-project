package com.expensetracker.model;

import java.time.LocalDate;

/**
 * Model class representing a single income entry.
 */
public class Income {
    private int id;
    private int userId;
    private double amount;
    private String source;
    private LocalDate date;
    private String description;

    public Income() {}

    public Income(int userId, double amount, String source, LocalDate date, String description) {
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.date = date;
        this.description = description;
    }

    public Income(int id, int userId, double amount, String source, LocalDate date, String description) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
