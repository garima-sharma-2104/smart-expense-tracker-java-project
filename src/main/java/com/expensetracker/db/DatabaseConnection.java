package com.expensetracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * <p>The class follows the <strong>Singleton</strong> pattern so every
 * component in the app shares the same configuration without passing
 * constructor arguments around.</p>
 */
public class DatabaseConnection {

    // ── Configuration ────────────────────────────────────────

    /** JDBC URL — the file is created automatically by SQLite if absent. */
    private static final String DB_URL = "jdbc:sqlite:expense_tracker.db";

    /** Singleton holder. */
    private static DatabaseConnection instance;

    // ── Constructor ──────────────────────────────────────────

    private DatabaseConnection() {
        // Explicitly load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver");
            e.printStackTrace();
        }
    }

    // ── Singleton accessor ───────────────────────────────────

    /**
     * Returns the single {@code DatabaseConnection} instance.
     * Thread-safe thanks to {@code synchronized}.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // ── Public API ───────────────────────────────────────────

    /**
     * Opens a <strong>new</strong> connection to {@code expense_tracker.db}.
     *
     * <p>Callers <em>must</em> close the returned connection when done
     * (prefer {@code try-with-resources}).</p>
     *
     * @return a live JDBC {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Enable WAL mode for better concurrent-read performance
        try (var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA foreign_keys=ON;");
        }
        return conn;
    }

    /**
     * Returns the configured JDBC URL (useful for logging / diagnostics).
     */
    public String getDatabaseUrl() {
        return DB_URL;
    }

    /**
     * Quick connectivity check — opens and immediately closes a connection.
     *
     * @return {@code true} if the database is reachable
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
