package com.expensetracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.expensetracker.ui.ThemeManager.*;

/**
 * Left-hand sidebar that acts as the primary navigation.
 * Highlights the currently selected item and notifies
 * the parent frame when the user switches views.
 */
public class NavigationPanel extends JPanel {

    public interface NavigationListener {
        void onNavigate(String viewName);
        void onToggleTheme();
    }

    private final NavigationListener listener;
    private String activeView = "Dashboard";

    private static final String[] NAV_ITEMS = {
            "Dashboard", "SplitSphere", "Analytics", "Add Income", "Add Expense", "All Expenses", "Categories"
    };
    private static final String[] NAV_ICONS = {
            "📊", "🌍", "📈", "💵", "💸", "📋", "📂"
    };

    public NavigationPanel(NavigationListener listener) {
        this.listener = listener;
        buildUI();
    }

    public void refresh() {
        removeAll();
        setBackground(BG_SIDEBAR);
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        setBackground(BG_SIDEBAR);
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        setLayout(new BorderLayout());

        JPanel brandPanel = createBrandPanel();
        add(brandPanel, BorderLayout.NORTH);

        JPanel navItemsPanel = new JPanel();
        navItemsPanel.setLayout(new BoxLayout(navItemsPanel, BoxLayout.Y_AXIS));
        navItemsPanel.setBackground(BG_SIDEBAR);
        navItemsPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < NAV_ITEMS.length; i++) {
            navItemsPanel.add(createNavButton(NAV_ICONS[i], NAV_ITEMS[i]));
            navItemsPanel.add(Box.createVerticalStrut(6));
        }

        add(navItemsPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SIDEBAR);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("💰");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Expense");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Tracker");
        subtitle.setFont(FONT_HEADING);
        subtitle.setForeground(ACCENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(8));
        panel.add(title);
        panel.add(subtitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(16));
        panel.add(sep);

        return panel;
    }

    private JPanel createNavButton(String icon, String label) {
        JPanel btn = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (label.equals(activeView)) {
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_ARC, BUTTON_ARC);
                } else if (getClientProperty("hover") == Boolean.TRUE) {
                    g2.setColor(BG_CARD_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_ARC, BUTTON_ARC);
                }
                g2.dispose();
            }
        };

        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon + "  ");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setForeground(label.equals(activeView) ? Color.WHITE : TEXT_SECONDARY);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(FONT_NAV);
        textLabel.setForeground(label.equals(activeView) ? Color.WHITE : TEXT_SECONDARY);

        btn.add(iconLabel, BorderLayout.WEST);
        btn.add(textLabel, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.putClientProperty("hover", Boolean.TRUE);
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.putClientProperty("hover", Boolean.FALSE);
                btn.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!activeView.equals(label)) {
                    activeView = label;
                    listener.onNavigate(label);
                    NavigationPanel.this.refresh();
                }
            }
        });

        return btn;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SIDEBAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);

        panel.add(Box.createVerticalStrut(12));

        JLabel version = new JLabel("v1.0.0");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_MUTED);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(version, BorderLayout.WEST);
        
        JButton themeToggleBtn = new JButton(ThemeManager.isDarkMode ? "☀️ Light" : "🌙 Dark");
        ThemeManager.styleButton(themeToggleBtn);
        themeToggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggleBtn.addActionListener(e -> {
            listener.onToggleTheme();
        });
        
        bottomRow.add(themeToggleBtn, BorderLayout.EAST);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(bottomRow);

        return panel;
    }
}
