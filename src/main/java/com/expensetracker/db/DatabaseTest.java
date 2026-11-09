package com.expensetracker.db;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * Standalone test harness that verifies the full database stack:
 *
 * <ol>
 *   <li>Opens a connection via {@link DatabaseConnection}</li>
 *   <li>Initialises both tables via {@link DatabaseManager}</li>
 *   <li>Inserts a user and an expense via the service layer</li>
 *   <li>Fetches data back and prints it to the console</li>
 * </ol>
 *
 * <p>Run directly: {@code test.bat}</p>
 */
public class DatabaseTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  Smart Expense Tracker -- DB Test Suite");
        System.out.println("==============================================");
        System.out.println();

        // -- Test 1: Connection --
        print("Test 1: Database Connection");
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        boolean connected = dbConn.testConnection();
        if (connected) {
            pass("Connection to " + dbConn.getDatabaseUrl() + " succeeded.");
        } else {
            fail("Connection FAILED.");
        }

        // -- Test 2: Table initialisation --
        print("Test 2: Table Initialisation");
        try {
            DatabaseManager.getInstance().initializeDatabase();
            pass("Tables created / verified.");
        } catch (Exception e) {
            fail("Table creation FAILED: " + e.getMessage());
        }

        // -- Test 3: Insert user --
        print("Test 3: Insert User");
        UserService userService = new UserService();
        User testUser = new User("Alice Johnson", "alice@example.com", "securePass123");
        int userId = userService.addUser(testUser);
        if (userId > 0) {
            pass("User inserted with id = " + userId);
        } else {
            // Might exist from a previous run
            User existing = userService.getUserByEmail("alice@example.com");
            if (existing != null) {
                userId = existing.getId();
                pass("User already exists (id = " + userId + "). Reusing.");
            } else {
                fail("User insert FAILED.");
            }
        }

        // -- Test 4: Fetch user --
        print("Test 4: Fetch User by ID (" + userId + ")");
        User fetched = userService.getUserById(userId);
        if (fetched != null) {
            pass("Fetched: " + fetched);
        } else {
            fail("Fetch FAILED for id = " + userId);
        }

        // -- Test 5: Insert expense --
        print("Test 5: Insert Expense");
        ExpenseService expenseService = new ExpenseService();
        Expense testExpense = new Expense(userId, 249.99, "Food & Dining",
                LocalDate.now(), "Dinner at Italian restaurant");
        boolean inserted = expenseService.addExpense(testExpense);
        if (inserted) {
            pass("Expense inserted successfully.");
        } else {
            fail("Expense insert FAILED.");
        }

        // -- Test 6: Fetch expenses by user --
        print("Test 6: Fetch Expenses by User ID (" + userId + ")");
        List<Expense> userExpenses = expenseService.getExpensesByUserId(userId);
        if (!userExpenses.isEmpty()) {
            pass("Retrieved " + userExpenses.size() + " expense(s) for user " + userId + ":");
            for (Expense e : userExpenses) {
                System.out.println("       -> " + e);
            }
        } else {
            fail("No expenses found for user " + userId + ".");
        }

        // -- Test 7: Total expenses --
        print("Test 7: Total Expenses by User ID (" + userId + ")");
        double total = expenseService.getTotalExpenses(userId);
        pass("Total = Rs " + String.format("%.2f", total));

        // -- Summary --
        System.out.println();
        System.out.println("==============================================");
        System.out.printf("  Results:  %d passed  |  %d failed  |  %d total%n", passed, failed, passed + failed);
        System.out.println("==============================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void print(String msg) {
        System.out.println();
        System.out.println("[>] " + msg);
    }

    private static void pass(String msg) {
        passed++;
        System.out.println("  [PASS] " + msg);
    }

    private static void fail(String msg) {
        failed++;
        System.out.println("  [FAIL] " + msg);
    }
}
