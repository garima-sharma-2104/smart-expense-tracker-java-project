package com.expensetracker.service;

import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer that provides CRUD operations for {@link Expense} objects.
 *
 * <p>Updated to work with the new schema:
 * {@code expenses(id, user_id, amount, category, date, description)}</p>
 */
public class ExpenseService {

    private final DatabaseConnection dbConnection;

    public ExpenseService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // ── CREATE ─────────────────────────────────────────────────

    /**
     * Inserts a new expense into the database.
     *
     * @return true if the insert succeeded
     */
    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, amount, category, date, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expense.getUserId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDate().toString());
            pstmt.setString(5, expense.getDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            return false;
        }
    }

    // ── READ ───────────────────────────────────────────────────

    // `getAllExpenses` was removed in favor of `getExpensesByUserId`

    /**
     * Returns expenses belonging to a specific user.
     */
    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expenses by user: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Returns expenses belonging to a specific user whose category matches the given value.
     */
    public List<Expense> getExpensesByCategory(int userId, String category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? AND category = ? ORDER BY date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expenses by category: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Returns the total amount of all expenses for a specific user.
     */
    public double getTotalExpenses(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Returns total expenses grouped by category for a specific user.
     */
    public List<Object[]> getExpensesByGroupedCategory(int userId) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = ? GROUP BY category ORDER BY total DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("category"), rs.getDouble("total")});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching grouped expenses: " + e.getMessage());
        }
        return results;
    }

    // ── UPDATE ─────────────────────────────────────────────────

    /**
     * Updates an existing expense identified by its id.
     *
     * @return true if the update succeeded
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET user_id = ?, amount = ?, category = ?, date = ?, description = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expense.getUserId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDate().toString());
            pstmt.setString(5, expense.getDescription());
            pstmt.setInt(6, expense.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────

    /**
     * Deletes the expense with the given id.
     *
     * @return true if a row was deleted
     */
    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns total expenses grouped by month for a specific user.
     */
    public List<Object[]> getMonthlyExpenseSummary(int userId) {
        List<Object[]> results = new ArrayList<>();
        // STRFTIME is used to format the date string to YYYY-MM
        String sql = "SELECT strftime('%Y-%m', date) as month, SUM(amount) AS total FROM expenses WHERE user_id = ? GROUP BY month ORDER BY month";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("month"), rs.getDouble("total")});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching monthly expenses: " + e.getMessage());
        }
        return results;
    }

    // ── Helpers ────────────────────────────────────────────────

    private Expense mapRow(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getDouble("amount"),
                rs.getString("category"),
                LocalDate.parse(rs.getString("date")),
                rs.getString("description")
        );
    }
}
