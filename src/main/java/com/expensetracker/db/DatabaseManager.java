package com.expensetracker.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database schema initialization.
 *
 * <p>All raw connection logic is delegated to {@link DatabaseConnection};
 * this class is responsible only for creating / migrating tables.</p>
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private final DatabaseConnection dbConnection;

    private DatabaseManager() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /** Returns the singleton instance. */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Convenience shortcut so that existing code that called
     * {@code DatabaseManager.getInstance().getConnection()} keeps working.
     */
    public Connection getConnection() throws SQLException {
        return dbConnection.getConnection();
    }

    // ── Schema initialisation ────────────────────────────────

    /**
     * Creates the {@code users} and {@code expenses} tables if they
     * do not already exist.
     */
    public void initializeDatabase() {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    name     TEXT    NOT NULL,
                    email    TEXT    NOT NULL UNIQUE,
                    password TEXT    NOT NULL
                );
                """;

        String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id     INTEGER NOT NULL,
                    amount      REAL    NOT NULL,
                    category    TEXT    NOT NULL,
                    date        TEXT    NOT NULL,
                    description TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        String createIncomesTable = """
                CREATE TABLE IF NOT EXISTS incomes (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id     INTEGER NOT NULL,
                    amount      REAL    NOT NULL,
                    source      TEXT    NOT NULL,
                    date        TEXT    NOT NULL,
                    description TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        String createGroupsTable = """
                CREATE TABLE IF NOT EXISTS groups (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    name        TEXT    NOT NULL,
                    created_by  INTEGER NOT NULL,
                    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        String createGroupMembersTable = """
                CREATE TABLE IF NOT EXISTS group_members (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_id    INTEGER NOT NULL,
                    user_id     INTEGER NOT NULL,
                    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE(group_id, user_id)
                );
                """;

        String createGroupExpensesTable = """
                CREATE TABLE IF NOT EXISTS group_expenses (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_id    INTEGER NOT NULL,
                    amount      REAL    NOT NULL,
                    paid_by     INTEGER NOT NULL,
                    date        TEXT    NOT NULL,
                    description TEXT,
                    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                    FOREIGN KEY (paid_by) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createExpensesTable);
            stmt.execute(createIncomesTable);
            stmt.execute(createGroupsTable);
            stmt.execute(createGroupMembersTable);
            stmt.execute(createGroupExpensesTable);

            System.out.println("✔ Database initialized — tables: users, expenses, incomes, groups, group_members, group_expenses");
        } catch (SQLException e) {
            System.err.println("✖ Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
