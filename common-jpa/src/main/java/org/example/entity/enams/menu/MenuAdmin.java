package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuAdmin {
    ListAllUsers(ButtonLabelManager.listAllUsers),
    BanUser(ButtonLabelManager.banUser),
    UnbanUser(ButtonLabelManager.unbanUser),
    TradingStatsAllUsers(ButtonLabelManager.tradingStatsAllUsers),
    TradingStatsSpecificUser(ButtonLabelManager.tradingStatsSpecificUser),
    StopTradingAllUsers(ButtonLabelManager.stopTradingAllUsers),
    GrantAdminRights(ButtonLabelManager.grantAdminRights),
    MainMenu(ButtonLabelManager.mainMenu),
    Cache(ButtonLabelManager.cancelCache);

    private final String value;

    MenuAdmin(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static MenuAdmin fromValue(String v) {
        for (MenuAdmin c : MenuAdmin.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
