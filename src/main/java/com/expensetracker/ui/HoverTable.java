package com.expensetracker.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.expensetracker.ui.ThemeManager.*;

public class HoverTable extends JTable {

    private int hoveredRow = -1;

    public HoverTable(TableModel dm) {
        super(dm);
        initHoverEffects();
    }

    private void initHoverEffects() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                repaint();
            }
        });
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (isRowSelected(row)) {
            c.setBackground(ACCENT_DARK);
            c.setForeground(Color.WHITE);
        } else if (row == hoveredRow) {
            c.setBackground(BG_CARD_HOVER);
            c.setForeground(TEXT_PRIMARY);
        } else {
            c.setBackground(BG_CARD);
            c.setForeground(TEXT_PRIMARY);
        }

        return c;
    }
}

