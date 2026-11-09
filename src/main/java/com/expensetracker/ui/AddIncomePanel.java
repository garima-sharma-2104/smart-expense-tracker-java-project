package com.expensetracker.ui;

import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.service.IncomeService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import static com.expensetracker.ui.ThemeManager.*;

public class AddIncomePanel extends JPanel {

    private final User user;
    private final IncomeService incomeService;
    private final Runnable onIncomeAdded;

    private JTextField amountField;
    private JTextField sourceField;
    private JTextField dateField;
    private JTextArea descriptionArea;

    public AddIncomePanel(User user, IncomeService incomeService, Runnable onIncomeAdded) {
        this.user = user;
        this.incomeService = incomeService;
        this.onIncomeAdded = onIncomeAdded;

        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        setLayout(new BorderLayout());

        buildUI();
    }

    public void refresh() {
        removeAll();
        setBackground(BG_MAIN);
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Log New Income");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(heading);
        centerPanel.add(Box.createVerticalStrut(40));

        amountField = new JTextField();
        sourceField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        descriptionArea = new JTextArea(4, 20);

        centerPanel.add(createInputGroup("Amount (₹)", amountField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createInputGroup("Source", sourceField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createInputGroup("Date (YYYY-MM-DD)", dateField));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createTextAreaGroup("Description (Optional)", descriptionArea));

        centerPanel.add(Box.createVerticalStrut(40));

        JPanel saveBtn = createSaveButton();
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(saveBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(centerPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createInputGroup(String labelText, JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setMaximumSize(new Dimension(400, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(FONT_BODY);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(400, 40));

        group.add(label);
        group.add(Box.createVerticalStrut(8));
        group.add(field);

        return group;
    }

    private JPanel createTextAreaGroup(String labelText, JTextArea area) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setMaximumSize(new Dimension(400, 120));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        area.setFont(FONT_BODY);
        area.setBackground(BG_INPUT);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(TEXT_PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(400, 80));

        group.add(label);
        group.add(Box.createVerticalStrut(8));
        group.add(scrollPane);

        return group;
    }

    private JPanel createSaveButton() {
        return ThemeManager.createStyledButton("Save Income", BTN_BG, this::saveIncome);
    }

    private void saveIncome() {
        if (amountField.getText().trim().isEmpty() || sourceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Amount and Source are required.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Amount must be a valid number.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Date must be in YYYY-MM-DD format.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String source = sourceField.getText().trim();
        String description = descriptionArea.getText().trim();

        Income income = new Income(user.getId(), amount, source, date, description);
        if (incomeService.addIncome(income)) {
            JOptionPane.showMessageDialog(this,
                    "Income saved successfully! ✓",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            amountField.setText("");
            sourceField.setText("");
            dateField.setText(LocalDate.now().toString());
            descriptionArea.setText("");

            if (onIncomeAdded != null) {
                onIncomeAdded.run();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save income. Check logs.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

