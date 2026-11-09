package com.expensetracker.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.expensetracker.ui.ThemeManager.*;

public class BarChartPanel extends JPanel {

    private final List<ChartItem> items = new ArrayList<>();
    private final String title;
    private final Color barColor;

    public BarChartPanel(String title, Color barColor) {
        this.title = title;
        this.barColor = barColor;
        setOpaque(false);
    }

    public void setData(Map<String, Double> rawData) {
        items.clear();
        for (Map.Entry<String, Double> entry : rawData.entrySet()) {
            items.add(new ChartItem(entry.getKey(), entry.getValue()));
        }
        repaint();
    }

    public void setDataList(List<Object[]> rawData) {
        items.clear();
        for (Object[] row : rawData) {
            items.add(new ChartItem(String.valueOf(row[0]), ((Number) row[1]).doubleValue()));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(BG_CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), CARD_ARC, CARD_ARC);

        // Title
        g2.setColor(TEXT_PRIMARY);
        g2.setFont(FONT_HEADING);
        g2.drawString(title, 20, 30);

        if (items.isEmpty()) {
            g2.setColor(TEXT_MUTED);
            g2.setFont(FONT_BODY);
            g2.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
            g2.dispose();
            return;
        }

        double maxVal = 0;
        for (ChartItem item : items) {
            if (item.val > maxVal) maxVal = item.val;
        }
        if (maxVal == 0) maxVal = 1; // prevent divide by zero

        int padding = 40;
        int topPadding = 60; // Leave space for title
        int chartWidth = getWidth() - padding * 2;
        int chartHeight = getHeight() - padding - topPadding;
        
        int barWidth = Math.max(20, (chartWidth / items.size()) - 10);
        int spacing = (chartWidth - (barWidth * items.size())) / (items.size() + 1);

        int currentX = padding + spacing;

        // Draw Axes
        g2.setColor(BORDER);
        g2.drawLine(padding, topPadding, padding, topPadding + chartHeight); // Y
        g2.drawLine(padding, topPadding + chartHeight, padding + chartWidth, topPadding + chartHeight); // X

        g2.setFont(FONT_SMALL);

        for (ChartItem item : items) {
            int barHeight = (int) ((item.val / maxVal) * chartHeight);
            int y = topPadding + chartHeight - barHeight;

            // Bar
            g2.setColor(barColor);
            g2.fillRoundRect(currentX, y, barWidth, barHeight, 6, 6);

            // Hide bottom rounded corners by drawing a rectangle over them
            if (barHeight > 6) {
                g2.fillRect(currentX, y + barHeight - 6, barWidth, 6);
            }

            // Label X-Axis
            g2.setColor(TEXT_SECONDARY);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(item.label);
            int lblX = currentX + (barWidth - labelWidth) / 2;
            g2.drawString(item.label, lblX, topPadding + chartHeight + 20);

            // Value Tag
            g2.setColor(TEXT_PRIMARY);
            String valStr = String.format("%.0f", item.val);
            int valWidth = fm.stringWidth(valStr);
            int valX = currentX + (barWidth - valWidth) / 2;
            g2.drawString(valStr, valX, y - 5);

            currentX += barWidth + spacing;
        }

        g2.dispose();
    }

    private record ChartItem(String label, double val) {}
}

