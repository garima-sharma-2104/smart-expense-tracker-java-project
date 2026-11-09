package com.expensetracker.ui;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import static com.expensetracker.ui.ThemeManager.*;

/**
 * Shows every expense in a styled table with a delete action.
 */
public class ExpenseListPanel extends JPanel {

    private final User user;
    private final ExpenseService expenseService;
    private final Runnable onDataChanged;
    private DefaultTableModel tableModel;
    private JTable table;

    public ExpenseListPanel(User user, ExpenseService expenseService, Runnable onDataChanged) {
        this.user = user;
        this.expenseService = expenseService;
        this.onDataChanged = onDataChanged;

        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        setLayout(new BorderLayout(0, 20));

        buildUI();
    }

    /** Reload data from the database. */
    public void refresh() {
        removeAll();
        setBackground(BG_MAIN);
        buildUI();
        revalidate();
        repaint();
    }

    // ── Build ─────────────────────────────────────────────────

    private void buildUI() {
        // Header row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JLabel heading = new JLabel("All Expenses");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_PRIMARY);
        headerRow.add(heading, BorderLayout.WEST);

        add(headerRow, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "User ID", "Amount (₹)", "Category", "Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new HoverTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(BG_CARD);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);
        JPanel editBtn = createActionButton("Edit Selected", WARNING, this::handleEdit);
        JPanel deleteBtn = createActionButton("Delete Selected", DANGER, this::handleDelete);
        bottomBar.add(editBtn);
        bottomBar.add(deleteBtn);
        add(bottomBar, BorderLayout.SOUTH);

        loadData();
    }

    // ── Table Styling ─────────────────────────────────────────

    private void styleTable(JTable tbl) {
        ThemeManager.styleTable(tbl);

        // Hide the ID column from view (but keep it in the model)
        tbl.getColumnModel().getColumn(0).setMinWidth(0);
        tbl.getColumnModel().getColumn(0).setMaxWidth(0);
        tbl.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Hide the UserID column as well
        tbl.getColumnModel().getColumn(1).setMinWidth(0);
        tbl.getColumnModel().getColumn(1).setMaxWidth(0);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(0);

        // Cell renderer for currency column (now index 2)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tbl.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
    }

    // ── Data ──────────────────────────────────────────────────

    private void loadData() {
        List<Expense> expenses = expenseService.getExpensesByUserId(user.getId());
        for (Expense e : expenses) {
            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getUserId(),
                    String.format("%.2f", e.getAmount()),
                    e.getCategory(),
                    e.getDate().toString(),
                    e.getDescription() != null ? e.getDescription() : ""
            });
        }
    }

    // ── Actions ───────────────────────────────────────────────

    private JPanel createActionButton(String text, Color color, Runnable onClick) {
        return ThemeManager.createStyledButton(text, color, onClick);
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this expense?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && expenseService.deleteExpense(id)) {
            tableModel.removeRow(selectedRow);
            if (onDataChanged != null) onDataChanged.run();
        }
    }
    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a row to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int expenseUserId = (int) tableModel.getValueAt(selectedRow, 1);
        String currentAmount = String.valueOf(tableModel.getValueAt(selectedRow, 2)).replace(",", "");
        String currentCategory = (String) tableModel.getValueAt(selectedRow, 3);
        String currentDate = (String) tableModel.getValueAt(selectedRow, 4);
        String currentDesc = (String) tableModel.getValueAt(selectedRow, 5);

        JTextField amountField = new JTextField(currentAmount);
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
        categoryBox.setSelectedItem(Category.fromDisplayName(currentCategory));
        JTextField dateField = new JTextField(currentDate);
        JTextArea descArea = new JTextArea(currentDesc, 3, 20);

        amountField.setBackground(BG_INPUT); amountField.setForeground(TEXT_PRIMARY);
        ThemeManager.styleComboBox(categoryBox);
        dateField.setBackground(BG_INPUT); dateField.setForeground(TEXT_PRIMARY);
        descArea.setBackground(BG_INPUT); descArea.setForeground(TEXT_PRIMARY);

        Object[] message = {
                "Amount (₹):", amountField,
                "Category:", categoryBox,
                "Date (YYYY-MM-DD):", dateField,
                "Description:", new JScrollPane(descArea)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Expense", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                Category cat = (Category) categoryBox.getSelectedItem();
                String desc = descArea.getText().trim();

                Expense updated = new Expense(id, expenseUserId, amount, cat.getDisplayName(), date, desc);
                if (expenseService.updateExpense(updated)) {
                    refresh();
                    if (onDataChanged != null) onDataChanged.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

