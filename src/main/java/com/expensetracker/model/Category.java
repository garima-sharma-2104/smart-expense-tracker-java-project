package com.expensetracker.model;

/**
 * Predefined expense categories.
 */
public enum Category {

    FOOD("Food & Dining", "🍔"),
    TRANSPORT("Transport", "🚗"),
    ENTERTAINMENT("Entertainment", "🎬"),
    SHOPPING("Shopping", "🛍️"),
    BILLS("Bills & Utilities", "💡"),
    HEALTH("Health", "🏥"),
    EDUCATION("Education", "📚"),
    OTHER("Other", "📦");

    private final String displayName;
    private final String icon;

    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return icon + "  " + displayName;
    }

    /**
     * Returns the Category matching the given display name, or OTHER if not found.
     */
    public static Category fromDisplayName(String name) {
        for (Category c : values()) {
            if (c.displayName.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return OTHER;
    }
}
