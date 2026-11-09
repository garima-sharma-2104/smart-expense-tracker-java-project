package com.expensetracker.model;

import java.time.LocalDate;

/**
 * Model class representing a single expense entry.
 *
 * <p>Matches the {@code expenses} table schema:
 * {@code id, user_id, amount, category, date, description}.</p>
 */
public class Expense {

    private int id;
    private int userId;
    private double amount;
    private String category;
    private LocalDate date;
    private String description;

    // ── Constructors ──────────────────────────────────────────

    public Expense() {
    }

    /** Create without id (for inserts). */
    public Expense(int userId, double amount, String category, LocalDate date, String description) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /** Create with id (for reads). */
    public Expense(int id, int userId, double amount, String category, LocalDate date, String description) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // ── Getters & Setters ─────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return String.format("Expense{id=%d, userId=%d, amount=%.2f, category='%s', date=%s, description='%s'}",
                id, userId, amount, category, date, description);
    }
}
