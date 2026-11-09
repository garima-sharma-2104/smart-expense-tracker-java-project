package com.expensetracker.service;

import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.model.Income;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeService {
    private final DatabaseConnection dbConnection;

    public IncomeService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean addIncome(Income income) {
        String sql = "INSERT INTO incomes (user_id, amount, source, date, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, income.getUserId());
            pstmt.setDouble(2, income.getAmount());
            pstmt.setString(3, income.getSource());
            pstmt.setString(4, income.getDate().toString());
            pstmt.setString(5, income.getDescription());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding income: " + e.getMessage());
            return false;
        }
    }

    public List<Income> getIncomesByUserId(int userId) {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM incomes WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incomes.add(new Income(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("amount"),
                            rs.getString("source"),
                            LocalDate.parse(rs.getString("date")),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching incomes by user: " + e.getMessage());
        }
        return incomes;
    }

    public double getTotalIncome(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM incomes WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total income: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean updateIncome(Income income) {
        String sql = "UPDATE incomes SET amount = ?, source = ?, date = ?, description = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, income.getAmount());
            pstmt.setString(2, income.getSource());
            pstmt.setString(3, income.getDate().toString());
            pstmt.setString(4, income.getDescription());
            pstmt.setInt(5, income.getId());
            pstmt.setInt(6, income.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating income: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteIncome(int id) {
        String sql = "DELETE FROM incomes WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting income: " + e.getMessage());
            return false;
        }
    }
}
