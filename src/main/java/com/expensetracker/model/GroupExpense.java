package com.expensetracker.model;

import java.time.LocalDate;

public class GroupExpense {
    private int id;
    private int groupId;
    private double amount;
    private int paidBy;
    private LocalDate date;
    private String description;
    
    // Non-DB transient properties for UI ease
    private String paidByName;

    public GroupExpense(int id, int groupId, double amount, int paidBy, LocalDate date, String description) {
        this.id = id;
        this.groupId = groupId;
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
        this.description = description;
    }

    public GroupExpense(int groupId, double amount, int paidBy, LocalDate date, String description) {
        this.groupId = groupId;
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getPaidBy() { return paidBy; }
    public void setPaidBy(int paidBy) { this.paidBy = paidBy; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPaidByName() { return paidByName; }
    public void setPaidByName(String paidByName) { this.paidByName = paidByName; }
}
