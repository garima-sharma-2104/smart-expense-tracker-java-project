package com.expensetracker.service;

import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.model.Group;
import com.expensetracker.model.GroupExpense;
import com.expensetracker.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitSphereService {

    private final DatabaseConnection dbConnection;

    public SplitSphereService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // ── Groups ──────────────────────────────────────────────────

    public int createGroup(String name, int createdByUserId) {
        String insertGroup = "INSERT INTO groups (name, created_by) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, createdByUserId);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int groupId = keys.getInt(1);
                        // Add creator as the first member
                        addMemberToGroup(groupId, createdByUserId);
                        return groupId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
        }
        return -1;
    }

    public List<Group> getGroupsForUser(int userId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT g.id, g.name, g.created_by FROM groups g " +
                     "JOIN group_members gm ON g.id = gm.group_id " +
                     "WHERE gm.user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(new Group(rs.getInt("id"), rs.getString("name"), rs.getInt("created_by")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching groups for user: " + e.getMessage());
        }
        return groups;
    }

    // ── Members ─────────────────────────────────────────────────

    public boolean addMemberToGroup(int groupId, int userId) {
        String sql = "INSERT OR IGNORE INTO group_members (group_id, user_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
        return false;
    }

    public List<User> getGroupMembers(int groupId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.password FROM users u " +
                     "JOIN group_members gm ON u.id = gm.user_id " +
                     "WHERE gm.group_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching group members: " + e.getMessage());
        }
        return members;
    }

    // ── Expenses ────────────────────────────────────────────────

    public boolean addGroupExpense(GroupExpense expense) {
        String sql = "INSERT INTO group_expenses (group_id, amount, paid_by, date, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, expense.getGroupId());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setInt(3, expense.getPaidBy());
            pstmt.setString(4, expense.getDate().toString());
            pstmt.setString(5, expense.getDescription());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding group expense: " + e.getMessage());
        }
        return false;
    }

    public List<GroupExpense> getGroupExpenses(int groupId) {
        List<GroupExpense> expenses = new ArrayList<>();
        String sql = "SELECT ge.*, u.name as paid_by_name FROM group_expenses ge " +
                     "JOIN users u ON ge.paid_by = u.id " +
                     "WHERE ge.group_id = ? ORDER BY ge.date DESC, ge.id DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupExpense exp = new GroupExpense(
                            rs.getInt("id"),
                            rs.getInt("group_id"),
                            rs.getDouble("amount"),
                            rs.getInt("paid_by"),
                            LocalDate.parse(rs.getString("date")),
                            rs.getString("description")
                    );
                    exp.setPaidByName(rs.getString("paid_by_name"));
                    expenses.add(exp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching group expenses: " + e.getMessage());
        }
        return expenses;
    }

    // ── Mathematics ─────────────────────────────────────────────

    /**
     * Calculates the net balances for a given group.
     * Positive Balance -> Owed money (Paid more than fair share)
     * Negative Balance -> Owes money (Paid less than fair share)
     * @return Map of UserId to Balance
     */
    public Map<Integer, Double> calculateBalances(int groupId) {
        List<User> members = getGroupMembers(groupId);
        List<GroupExpense> expenses = getGroupExpenses(groupId);

        Map<Integer, Double> balances = new HashMap<>();
        for (User u : members) {
            balances.put(u.getId(), 0.0);
        }
        if (members.isEmpty()) return balances;

        for (GroupExpense expense : expenses) {
            double amount = expense.getAmount();
            double share = amount / members.size();

            // The person who paid getting credited
            int payerId = expense.getPaidBy();
            balances.put(payerId, balances.getOrDefault(payerId, 0.0) + amount);

            // Everyone owes their share
            for (User u : members) {
                balances.put(u.getId(), balances.get(u.getId()) - share);
            }
        }
        return balances;
    }

    /**
     * Calculates the total amount paid by each member in a group.
     */
    public Map<String, Double> getMemberSpending(int groupId) {
        Map<String, Double> spending = new HashMap<>(); // username -> amount
        String sql = "SELECT u.name, COALESCE(SUM(ge.amount), 0) as total " +
                "FROM users u " +
                "JOIN group_members gm ON u.id = gm.user_id " +
                "LEFT JOIN group_expenses ge ON ge.paid_by = u.id AND ge.group_id = gm.group_id " +
                "WHERE gm.group_id = ? " +
                "GROUP BY u.id, u.name";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    spending.put(rs.getString("name"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching member spending: " + e.getMessage());
        }
        return spending;
    }
}
