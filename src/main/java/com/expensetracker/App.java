package com.expensetracker;

import com.expensetracker.db.DatabaseManager;
import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import com.expensetracker.ui.AuthFrame;

import javax.swing.*;

/**
 * Application entry point for Smart Expense Tracker.
 */
public class App {

    public static void main(String[] args) {
        // Initialize database (creates tables if needed)
        DatabaseManager.getInstance().initializeDatabase();

        // Ensure a default user exists (id = 1) for the GUI
        ensureDefaultUser();

        // Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AuthFrame frame = new AuthFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Creates a default user if the users table is empty.
     * This keeps the GUI functional before a full login system is added.
     */
    private static void ensureDefaultUser() {
        UserService userService = new UserService();
        if (userService.getAllUsers().isEmpty()) {
            User defaultUser = new User("Default User", "user@expense.app", "password");
            int id = userService.addUser(defaultUser);
            System.out.println("✔ Created default user (id=" + id + ")");
        }
    }
}
