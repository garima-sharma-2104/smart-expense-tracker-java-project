package com.expensetracker.ui;

import com.expensetracker.model.Group;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.SplitSphereService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.expensetracker.ui.ThemeManager.*;

public class AnalyticsPanel extends JPanel {

    private final User currentUser;
    private final ExpenseService expenseService;
    private final SplitSphereService splitSphereService;

    private BarChartPanel monthlyChart;
    private BarChartPanel groupChart;
    private JComboBox<Group> groupComboBox;
    private JLabel highestCategoryLabel;

    public AnalyticsPanel(User currentUser, ExpenseService expenseService, SplitSphereService splitSphereService) {
        this.currentUser = currentUser;
        this.expenseService = expenseService;
        this.splitSphereService = splitSphereService;

        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setLayout(new BorderLayout(0, 20));

        buildUI();
    }

    public void refresh() {
        removeAll();
        setBackground(BG_MAIN);
        buildUI();

        // Refresh Monthly Data
        List<Object[]> monthlyData = expenseService.getMonthlyExpenseSummary(currentUser.getId());
        monthlyChart.setDataList(monthlyData);

        // Refresh Highest Category
        List<Object[]> grouped = expenseService.getExpensesByGroupedCategory(currentUser.getId());
        if (!grouped.isEmpty()) {
            highestCategoryLabel.setText("Highest spend: " + grouped.get(0)[0] + " (₹" + String.format("%.2f", grouped.get(0)[1]) + ")");
        } else {
            highestCategoryLabel.setText("Highest spend: None");
        }

        // Refresh Group List
        updateGroupComboBox();

        revalidate();
        repaint();
    }

    private void buildUI() {
        JLabel title = new JLabel("Analytics Overview");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        highestCategoryLabel = new JLabel("Highest spend: Loading...");
        highestCategoryLabel.setFont(FONT_SUBHEAD);
        highestCategoryLabel.setForeground(ACCENT); // More visible than WARNING on some dark bgs

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(highestCategoryLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center constraints for the two charts
        JPanel chartsContainer = new JPanel(new GridLayout(2, 1, 0, 20));
        chartsContainer.setOpaque(false);

        // 1. Monthly Chart
        monthlyChart = new BarChartPanel("Your Monthly Spending", ACCENT);
        
        // 2. Group Tracking Chart
        JPanel groupSection = new JPanel(new BorderLayout(0, 10));
        groupSection.setOpaque(false);

        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboWrapper.setOpaque(false);
        
        JLabel comboLabel = new JLabel("Viewing Group Member Spending:   ");
        comboLabel.setFont(FONT_SUBHEAD);
        comboLabel.setForeground(TEXT_SECONDARY);
        
        groupComboBox = new JComboBox<>();
        ThemeManager.styleComboBox(groupComboBox);
        groupComboBox.addActionListener(e -> updateGroupChart());

        comboWrapper.add(comboLabel);
        comboWrapper.add(groupComboBox);

        groupChart = new BarChartPanel("Group Gross Payments", SUCCESS);

        groupSection.add(comboWrapper, BorderLayout.NORTH);
        groupSection.add(groupChart, BorderLayout.CENTER);

        chartsContainer.add(monthlyChart);
        chartsContainer.add(groupSection);

        add(chartsContainer, BorderLayout.CENTER);
    }

    private void updateGroupComboBox() {
        groupComboBox.removeAllItems();
        List<Group> groups = splitSphereService.getGroupsForUser(currentUser.getId());
        for (Group g : groups) {
            groupComboBox.addItem(g);
        }
        updateGroupChart();
    }

    private void updateGroupChart() {
        Group selected = (Group) groupComboBox.getSelectedItem();
        if (selected != null) {
            Map<String, Double> spending = splitSphereService.getMemberSpending(selected.getId());
            groupChart.setData(spending);
        } else {
            groupChart.setData(Map.of()); // Clear
        }
    }
}

