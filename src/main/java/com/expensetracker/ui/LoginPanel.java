package com.expensetracker.ui;

import com.expensetracker.model.User;
import com.expensetracker.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.expensetracker.ui.ThemeManager.*;

public class LoginPanel extends JPanel {

    private final AuthFrame parentFrame;
    private final UserService userService;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(AuthFrame parentFrame, UserService userService) {
        this.parentFrame = parentFrame;
        this.userService = userService;

        setBackground(BG_MAIN);
        setLayout(new GridBagLayout()); // To center the login card
        buildUI();
    }

    private void buildUI() {
        // Form card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_ARC, CARD_ARC);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Header Title
        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(FONT_HEADING);
        title.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Log in to track your expenses", SwingConstants.CENTER);
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(subtitle, gbc);

        // Reset insets
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 0, 8, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Email Field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        card.add(createLabel("Email"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emailField = createTextField("user@example.com");
        card.add(emailField, gbc);

        // Password Field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        card.add(createLabel("Password"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        card.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel loginBtn = createButton("Log In");
        card.add(loginBtn, gbc);

        // Link to Signup
        JLabel signupLink = new JLabel("Don't have an account? Sign up");
        signupLink.setFont(FONT_BODY);
        signupLink.setForeground(ACCENT);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showSignup();
            }
        });
        gbc.gridy = 5;
        card.add(signupLink, gbc);

        add(card);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBHEAD);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        styleTextField(field);
        field.setText(placeholder);
        field.setForeground(TEXT_MUTED);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
    }

    private JPanel createButton(String text) {
        return ThemeManager.createStyledButton(text, BTN_BG, this::handleLogin);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || email.equals("user@example.com") || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.authenticate(email, password);
        if (user != null) {
            parentFrame.onLoginSuccess(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}

