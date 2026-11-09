package com.expensetracker.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public final class ThemeManager {

    private ThemeManager() {}

    public static boolean isDarkMode = true;

    // Backgrounds
    public static Color BG_MAIN;
    public static Color BG_PANEL;
    public static Color BG_SIDEBAR; // synonymous with BG_PANEL based on new theme
    public static Color BG_CARD;    // synonymous with BG_PANEL
    public static Color BG_CARD_HOVER;
    public static Color BG_INPUT;

    // Text
    public static Color TEXT_PRIMARY;
    public static Color TEXT_SECONDARY;
    public static Color TEXT_MUTED;

    // Accents
    public static Color ACCENT;
    public static Color ACCENT_LIGHT;
    public static Color ACCENT_DARK;
    
    // Status
    public static Color SUCCESS;
    public static Color WARNING;
    public static Color DANGER;

    // Border
    public static Color BORDER;

    // Buttons
    public static Color BTN_BG;
    public static Color BTN_TEXT;
    public static Color BTN_HOVER;

    static {
        applyTheme(true); // default dark
    }

    public static void applyTheme(boolean dark) {
        isDarkMode = dark;
        if (dark) {
            BG_MAIN = Color.decode("#0f172a");
            BG_PANEL = Color.decode("#1e293b");
            BG_SIDEBAR = BG_PANEL;
            BG_CARD = BG_PANEL;
            BG_CARD_HOVER = Color.decode("#2d3748"); // slightly lighter for hover
            BG_INPUT = Color.decode("#1e293b");

            TEXT_PRIMARY = Color.decode("#f8fafc");
            TEXT_SECONDARY = Color.decode("#cbd5f5");
            TEXT_MUTED = Color.decode("#a5b4fc"); // Lighter/Indigo tint instead of grey

            ACCENT = new Color(0x6C63FF);
            ACCENT_LIGHT = new Color(0x8B83FF);
            ACCENT_DARK = new Color(0x4F46E5);

            SUCCESS = new Color(0x22C55E);
            WARNING = new Color(0xFBBF24); // Brighter amber for dark mode
            DANGER = new Color(0xEF4444);

            BORDER = new Color(0x3B3D5C);

            BTN_BG = Color.decode("#6366f1");
            BTN_TEXT = Color.WHITE;
            BTN_HOVER = Color.decode("#4f46e5");
        } else {
            BG_MAIN = Color.decode("#f5f7fa");
            BG_PANEL = Color.decode("#ffffff");
            BG_SIDEBAR = BG_PANEL;
            BG_CARD = BG_PANEL;
            BG_CARD_HOVER = Color.decode("#f1f5f9"); // slightly darker for hover
            BG_INPUT = Color.decode("#ffffff");

            TEXT_PRIMARY = Color.decode("#1e293b");
            TEXT_SECONDARY = Color.decode("#475569");
            TEXT_MUTED = Color.decode("#94a3b8");

            ACCENT = new Color(0x4F46E5);
            ACCENT_LIGHT = new Color(0x6C63FF);
            ACCENT_DARK = new Color(0x3730A3);

            SUCCESS = new Color(0x16A34A);
            WARNING = new Color(0xD97706);
            DANGER = new Color(0xDC2626);

            BORDER = new Color(0xE5E7EB);

            BTN_BG = Color.decode("#4f46e5");
            BTN_TEXT = Color.WHITE;
            BTN_HOVER = Color.decode("#4338ca");
        }

        updateUIManager();
    }

    public static void toggleTheme() {
        applyTheme(!isDarkMode);
    }

    private static void updateUIManager() {
        // Global UIManager definitions
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("Label.foreground", TEXT_SECONDARY); // Default labels to secondary
        
        // Input fields styling
        UIManager.put("TextField.background", BG_INPUT);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.background", BG_INPUT);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        
        // List/Dropdown elements (fixes Category dropdown contrast)
        UIManager.put("List.background", BG_INPUT);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", ACCENT);
        UIManager.put("List.selectionForeground", Color.WHITE);
        
        UIManager.put("PopupMenu.background", BG_INPUT);
        UIManager.put("PopupMenu.foreground", TEXT_PRIMARY);
        
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground", TEXT_PRIMARY);
        
        // (Removed global Button.* overrides to prevent breaking native OptionPane dialogs; 
        // custom buttons are already handled manually via ThemeManager.styleButton)
        
        // Table styling
        UIManager.put("Table.background", BG_PANEL);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("Table.selectionBackground", ACCENT_DARK);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        
        UIManager.put("TableHeader.background", BG_SIDEBAR);
        UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
        UIManager.put("TableHeader.font", FONT_SUBHEAD);
        
        UIManager.put("ScrollPane.background", BG_PANEL);
        UIManager.put("Viewport.background", BG_PANEL);
    }

    // ── Typography ────────────────────────────────────────────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  22); // Increased size
    public static final Font FONT_SUBHEAD   = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.BOLD, 13); // Bolded for visibility
    public static final Font FONT_NAV       = new Font("Segoe UI", Font.BOLD,  15);

    // ── Dimensions & Utilities ────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH   = 240;
    public static final int CARD_ARC        = 18;
    public static final int BUTTON_ARC      = 12;
    public static final Insets CARD_PADDING  = new Insets(24, 28, 24, 28);
    
    public static Border createInputBorder() {
        return new CompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static Border createErrorBorder() {
        return new CompoundBorder(
            BorderFactory.createLineBorder(DANGER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setBackground(BG_INPUT);
        box.setForeground(TEXT_PRIMARY);
        box.setBorder(createInputBorder());
        
        // Force basic UI to prevent native LookAndFeel from painting white backgrounds
        box.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("\u25BC");
                button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                button.setBackground(BG_INPUT);
                button.setForeground(TEXT_MUTED);
                button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return button;
            }
        });

        // Custom renderer to force contrast on all Look & Feels
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(ACCENT);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(BG_INPUT);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }

    /** Styles a standard JButton to match the design system. */
    public static void styleButton(JButton btn) {
        btn.setFont(FONT_NAV);
        btn.setBackground(BTN_BG);
        btn.setForeground(BTN_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Creates a highly customized, rounded JPanel button. */
    public static JPanel createStyledButton(String text, Color bgColor, Runnable onClick) {
        JPanel btn = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color currentBg = getClientProperty("hover") == Boolean.TRUE ? 
                    (bgColor == BTN_BG ? BTN_HOVER : bgColor.brighter()) : bgColor;
                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_ARC, BUTTON_ARC);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setLayout(new GridBagLayout());

        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_NAV);
        lbl.setForeground(Color.WHITE);
        btn.add(lbl);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.putClientProperty("hover", Boolean.TRUE);
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.putClientProperty("hover", Boolean.FALSE);
                btn.repaint();
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });

        return btn;
    }

    /** Centralized JTable stylling for all screens. */
    public static void styleTable(JTable tbl) {
        tbl.setBackground(BG_CARD);
        tbl.setForeground(TEXT_PRIMARY);
        tbl.setFont(FONT_BODY);
        tbl.setRowHeight(40);
        tbl.setGridColor(BORDER);
        tbl.setSelectionBackground(ACCENT_DARK);
        tbl.setSelectionForeground(Color.WHITE);
        tbl.setShowHorizontalLines(true);
        tbl.setShowVerticalLines(false);
        tbl.setIntercellSpacing(new Dimension(0, 1));

        javax.swing.table.JTableHeader header = tbl.getTableHeader();
        header.setBackground(BG_SIDEBAR);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(FONT_SUBHEAD);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        
        // Force the Look & Feel to paint the header with our custom background and text properties
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(BG_SIDEBAR);
                c.setForeground(TEXT_SECONDARY);
                c.setFont(FONT_SUBHEAD);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return c;
            }
        });
    }
}

