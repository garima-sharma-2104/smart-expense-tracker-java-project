package com.expensetracker.ui;

import com.expensetracker.model.User;
import com.expensetracker.service.UserService;

import javax.swing.*;
import java.awt.*;

import static com.expensetracker.ui.ThemeManager.*;

public class AuthFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final UserService userService = new UserService();

    public AuthFrame() {
        setTitle("Smart Expense Tracker - Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null); // Center on screen

        getContentPane().setBackground(BG_MAIN);

        contentPanel.setBackground(BG_MAIN);
        contentPanel.add(new LoginPanel(this, userService), "Login");
        contentPanel.add(new SignupPanel(this, userService), "Signup");

        add(contentPanel, BorderLayout.CENTER);
    }

    public void showLogin() {
        cardLayout.show(contentPanel, "Login");
    }

    public void showSignup() {
        cardLayout.show(contentPanel, "Signup");
    }

    public void onLoginSuccess(User user) {
        // Close this auth frame
        this.dispose();

        // Launch the main application frame with the context of the logged-in user
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        });
    }
}

