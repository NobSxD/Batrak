package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuStatistics {
    Today(ButtonLabelManager.today),
    Week(ButtonLabelManager.thisOfWeek),
    Month(ButtonLabelManager.thisOfMonth),
    HalfYear(ButtonLabelManager.thisHalfYear),
    Year(ButtonLabelManager.thisYear),
    MainMenu(ButtonLabelManager.mainMenu);

    private final String value;
    MenuStatistics(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    public static MenuStatistics fromValue(String v) {
        for (MenuStatistics c : MenuStatistics.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
