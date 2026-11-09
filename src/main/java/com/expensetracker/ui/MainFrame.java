package com.expensetracker.ui;

import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.IncomeService;
import com.expensetracker.service.SplitSphereService;

import javax.swing.*;
import java.awt.*;

import static com.expensetracker.ui.ThemeManager.*;

/**
 * The top-level graphical container representing the active application window.
 * Manages the user session and facilitates routing between the various functional
 * dashboards via a custom NavigationPanel side-menu.
 * 
 * UI Composition:
 * - Uses a BorderLayout to separate the NavigationPanel (WEST) from the content area (CENTER).
 * - The content area utilizes a CardLayout to switch between different functional panels.
 */
public class MainFrame extends JFrame implements NavigationPanel.NavigationListener {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel  = new JPanel(cardLayout);

    private final ExpenseService expenseService = new ExpenseService();
    private final IncomeService incomeService = new IncomeService();
    private final SplitSphereService splitSphereService = new SplitSphereService();

    // View panels
    private DashboardPanel   dashboardPanel;
    private SplitSphereDashboard splitSphereDashboard;
    private AnalyticsPanel    analyticsPanel;
    private AddIncomePanel    addIncomePanel;
    private AddExpensePanel   addExpensePanel;
    private ExpenseListPanel  expenseListPanel;
    private CategoryPanel     categoryPanel;

    private final User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Expense Tracker - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);   // center on screen
        getContentPane().setBackground(BG_MAIN);

        initPanels();
        layoutFrame();
    }

    // ── Initialisation ────────────────────────────────────────

    private void initPanels() {
        Runnable refreshAll = this::refreshAllPanels;

        dashboardPanel   = new DashboardPanel(currentUser, expenseService, incomeService);
        splitSphereDashboard = new SplitSphereDashboard(currentUser);
        analyticsPanel    = new AnalyticsPanel(currentUser, expenseService, splitSphereService);
        addIncomePanel    = new AddIncomePanel(currentUser, incomeService, refreshAll);
        addExpensePanel   = new AddExpensePanel(currentUser, expenseService, refreshAll);
        expenseListPanel  = new ExpenseListPanel(currentUser, expenseService, refreshAll);
        categoryPanel     = new CategoryPanel(currentUser, expenseService);
    }

    private void layoutFrame() {
        setLayout(new BorderLayout());

        // Sidebar
        NavigationPanel nav = new NavigationPanel(this);
        add(nav, BorderLayout.WEST);

        // Content area (card layout)
        contentPanel.setBackground(BG_MAIN);
        contentPanel.add(dashboardPanel,   "Dashboard");
        contentPanel.add(splitSphereDashboard, "SplitSphere");
        contentPanel.add(analyticsPanel,    "Analytics");
        contentPanel.add(addIncomePanel,   "Add Income");
        contentPanel.add(addExpensePanel,   "Add Expense");
        contentPanel.add(expenseListPanel,  "All Expenses");
        contentPanel.add(categoryPanel,     "Categories");

        add(contentPanel, BorderLayout.CENTER);
    }

    // ── Navigation callback ───────────────────────────────────

    @Override
    public void onNavigate(String viewName) {
        cardLayout.show(contentPanel, viewName);

        // Refresh the target panel so it reflects latest data
        switch (viewName) {
            case "Dashboard"    -> dashboardPanel.refresh();
            case "SplitSphere"  -> splitSphereDashboard.refresh();
            case "Analytics"    -> analyticsPanel.refresh();
            case "All Expenses" -> expenseListPanel.refresh();
            case "Categories"   -> categoryPanel.refresh();
        }
    }

    @Override
    public void onToggleTheme() {
        toggleTheme();
        SwingUtilities.updateComponentTreeUI(this);
        getContentPane().setBackground(BG_MAIN);
        contentPanel.setBackground(BG_MAIN);
        
        refreshAllPanels();
        
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof NavigationPanel) {
                ((NavigationPanel) c).refresh();
            }
        }
        revalidate();
        repaint();
    }

    // ── Refresh helpers ───────────────────────────────────────

    private void refreshAllPanels() {
        dashboardPanel.refresh();
        splitSphereDashboard.refresh();
        analyticsPanel.refresh();
        addIncomePanel.refresh();
        addExpensePanel.refresh();
        expenseListPanel.refresh();
        categoryPanel.refresh();
    }
}

