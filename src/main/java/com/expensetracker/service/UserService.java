package com.expensetracker.service;

import com.expensetracker.db.DatabaseConnection;
import com.expensetracker.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer providing CRUD operations for {@link User} objects.
 */
public class UserService {

    private final DatabaseConnection dbConnection;

    public UserService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // ── CREATE ─────────────────────────────────────────────────

    /**
     * Inserts a new user and returns the generated id, or {@code -1} on failure.
     */
    public int addUser(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        user.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
        return -1;
    }

    // ── READ ───────────────────────────────────────────────────

    /**
     * Returns the user with the given id, or {@code null} if not found.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Authenticates a user by email and password.
     * @return the User object if successful, null otherwise.
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns the user with the given email, or {@code null} if not found.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all users ordered by name.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    // ── UPDATE ─────────────────────────────────────────────────

    /**
     * Updates an existing user identified by id.
     *
     * @return true if the update succeeded
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────

    /**
     * Deletes the user with the given id.
     *
     * @return true if a row was deleted
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // ── Helpers ────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
