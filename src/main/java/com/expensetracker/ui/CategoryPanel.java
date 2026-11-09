package com.expensetracker.ui;

import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.expensetracker.ui.ThemeManager.*;

/**
 * Panel that shows per-category totals as visual cards.
 */
public class CategoryPanel extends JPanel {

    private final User user;
    private final ExpenseService expenseService;

    private static final Color[] CARD_COLORS = {
            new Color(0x6C63FF), new Color(0x22C55E), new Color(0xF59E0B),
            new Color(0xEF4444), new Color(0x06B6D4), new Color(0xEC4899),
            new Color(0xA855F7), new Color(0x14B8A6)
    };
    private JPanel cardsPanel;

    public CategoryPanel(User user, ExpenseService expenseService) {
        this.user = user;
        this.expenseService = expenseService;
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        setLayout(new BorderLayout(0, 24));

        buildUI();
    }

    /** Rebuilds the panel from the database. */
    public void refresh() {
        removeAll();
        setBackground(BG_MAIN);
        buildUI();
        revalidate();
        repaint();
    }

    // ── Build ─────────────────────────────────────────────────

    private void buildUI() {
        JLabel heading = new JLabel("Categories");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_PRIMARY);
        add(heading, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        cardsPanel.setOpaque(false);

        List<Object[]> grouped = expenseService.getExpensesByGroupedCategory(user.getId());

        if (grouped.isEmpty()) {
            // Show all predefined categories with ₹0
            int idx = 0;
            for (Category cat : Category.values()) {
                cardsPanel.add(createCategoryCard(cat.getDisplayName(), 0, CARD_COLORS[idx % CARD_COLORS.length]));
                idx++;
            }
        } else {
            int idx = 0;
            for (Object[] row : grouped) {
                String cat = (String) row[0];
                double amt = (Double) row[1];
                cardsPanel.add(createCategoryCard(cat, amt, CARD_COLORS[idx % CARD_COLORS.length]));
                idx++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ── Category Card ─────────────────────────────────────────

    private JPanel createCategoryCard(String name, double amount, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_ARC, CARD_ARC);

                // Accent strip on the left
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 90));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(FONT_HEADING);
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel amountLabel = new JLabel(String.format("₹ %.2f", amount));
        amountLabel.setFont(FONT_TITLE);
        amountLabel.setForeground(accent);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.SOUTH);

        return card;
    }
}

