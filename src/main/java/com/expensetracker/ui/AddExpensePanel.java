package com.expensetracker.ui;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.expensetracker.ui.ThemeManager.*;

/**
 * Form panel for adding a new expense.
 *
 * <p>Uses the updated {@link Expense} model with
 * {@code user_id} and {@code description} fields.</p>
 */
public class AddExpensePanel extends JPanel {

    private final User user;
    private final ExpenseService expenseService;
    private final Runnable onExpenseAdded;

    private JTextField amountField;
    private JComboBox<Category> categoryBox;
    private JTextField dateField;
    private JTextArea descriptionArea;

    /**
     * @param onExpenseAdded callback invoked after a successful insert (used to refresh dashboard)
     */
    public AddExpensePanel(User user, ExpenseService expenseService, Runnable onExpenseAdded) {
        this.user = user;
        this.expenseService = expenseService;
        this.onExpenseAdded = onExpenseAdded;

        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        setLayout(new BorderLayout(0, 24));

        buildUI();
    }

    // ── Build ─────────────────────────────────────────────────

    public void refresh() {
        removeAll();
        setBackground(BG_MAIN);
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        // Header
        JLabel heading = new JLabel("Add New Expense");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_PRIMARY);
        add(heading, BorderLayout.NORTH);

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
        card.setBorder(BorderFactory.createEmptyBorder(
                CARD_PADDING.top, CARD_PADDING.left,
                CARD_PADDING.bottom, CARD_PADDING.right));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 – Amount
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(createLabel("Amount (₹)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        amountField = createTextField("0.00");
        card.add(amountField, gbc);

        // Row 1 – Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(createLabel("Category"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        categoryBox = new JComboBox<>(Category.values());
        styleComboBox(categoryBox);
        card.add(categoryBox, gbc);

        // Row 2 – Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        card.add(createLabel("Date"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dateField = createTextField(LocalDate.now().toString());
        card.add(dateField, gbc);

        // Row 3 – Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        card.add(createLabel("Description"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(FONT_BODY);
        descriptionArea.setBackground(BG_INPUT);
        descriptionArea.setForeground(TEXT_PRIMARY);
        descriptionArea.setCaretColor(TEXT_PRIMARY);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setBorder(null);
        card.add(scroll, gbc);

        // Row 4 – Submit button
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JPanel submitBtn = createButton("Save Expense");
        card.add(submitBtn, gbc);

        add(card, BorderLayout.CENTER);
    }

    // ── Styled Components ─────────────────────────────────────

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBHEAD);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(FONT_BODY);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setToolTipText(placeholder);

        // Placeholder appearance
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

    private void styleComboBox(JComboBox<?> box) {
        ThemeManager.styleComboBox(box);
    }

    private JPanel createButton(String text) {
        return ThemeManager.createStyledButton(text, BTN_BG, this::handleSubmit);
    }

    // ── Form Submission ───────────────────────────────────────

    private void handleSubmit() {
        String amtStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Please enter a valid positive amount.");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            showError("Date must be in YYYY-MM-DD format.");
            return;
        }

        Category category = (Category) categoryBox.getSelectedItem();
        String description = descriptionArea.getText().trim();

        Expense expense = new Expense(user.getId(), amount, category.getDisplayName(), date, description);
        if (expenseService.addExpense(expense)) {
            JOptionPane.showMessageDialog(this,
                    "Expense saved successfully! ✓",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            if (onExpenseAdded != null) {
                onExpenseAdded.run();
            }
        } else {
            showError("Failed to save expense. Please try again.");
        }
    }

    private void clearForm() {
        amountField.setText("0.00");
        amountField.setForeground(TEXT_MUTED);
        dateField.setText(LocalDate.now().toString());
        dateField.setForeground(TEXT_MUTED);
        categoryBox.setSelectedIndex(0);
        descriptionArea.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}

