package com.expensetracker.ui;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.IncomeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.expensetracker.ui.ThemeManager.*;

public class DashboardPanel extends JPanel {

    private final User user;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    public DashboardPanel(User user, ExpenseService expenseService, IncomeService incomeService) {
        this.user = user;
        this.expenseService = expenseService;
        this.incomeService = incomeService;

        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setLayout(new BorderLayout(0, 24));

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
        // --- Top: Header + Summary Cards ---
        JPanel topWrapper = new JPanel(new BorderLayout(0, 16));
        topWrapper.setOpaque(false);

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_PRIMARY);
        topWrapper.add(heading, BorderLayout.NORTH);

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsRow.setOpaque(false);

        double totalIncome = incomeService.getTotalIncome(user.getId());
        double totalExpense = expenseService.getTotalExpenses(user.getId());
        double balance = totalIncome - totalExpense;

        cardsRow.add(createSummaryCard("Total Income", String.format("₹ %.2f", totalIncome), SUCCESS));
        cardsRow.add(createSummaryCard("Total Expenses", String.format("₹ %.2f", totalExpense), DANGER));
        cardsRow.add(createSummaryCard("Balance", String.format("₹ %.2f", balance), balance >= 0 ? ACCENT : WARNING));

        topWrapper.add(cardsRow, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // --- Center: Recent Transactions ---
        JPanel recentPanel = createRecentTransactionsPanel();
        add(recentPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background adapts to theme
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_ARC, CARD_ARC);

                // Accent top border
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SMALL);
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_TITLE);
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel heading = new JLabel("Recent Transactions");
        heading.setFont(FONT_HEADING);
        heading.setForeground(TEXT_PRIMARY);
        panel.add(heading, BorderLayout.NORTH);

        String[] columns = {"Date", "Type", "Category / Source", "Description", "Amount (₹)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new HoverTable(model);
        ThemeManager.styleTable(table);

        // ✅ Fix 1: Table fills space properly
        table.setFillsViewportHeight(true);

        // ✅ Fix 2: Header contrast fix
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TEXT_PRIMARY);

        List<Transaction> transactions = getMergedTransactions();
        for (Transaction t : transactions) {
            model.addRow(new Object[]{
                    t.date.toString(),
                    t.isIncome ? "Income" : "Expense",
                    t.category,
                    t.description != null ? t.description : "",
                    String.format("%.2f", t.amount)
            });
        }

        JScrollPane scroll = new JScrollPane(table);

        // ✅ Fix 3: Proper background for light/dark theme
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private List<Transaction> getMergedTransactions() {
        List<Expense> expenses = expenseService.getExpensesByUserId(user.getId());
        List<Income> incomes = incomeService.getIncomesByUserId(user.getId());

        List<Transaction> merged = new ArrayList<>();

        for (Expense e : expenses) {
            merged.add(new Transaction(e.getDate(), false, e.getCategory(), e.getDescription(), e.getAmount()));
        }

        for (Income i : incomes) {
            merged.add(new Transaction(i.getDate(), true, i.getSource(), i.getDescription(), i.getAmount()));
        }

        merged.sort(Comparator.comparing((Transaction t) -> t.date).reversed());

        return merged.size() > 10 ? merged.subList(0, 10) : merged;
    }

    private record Transaction(LocalDate date, boolean isIncome, String category, String description, double amount) {}
}