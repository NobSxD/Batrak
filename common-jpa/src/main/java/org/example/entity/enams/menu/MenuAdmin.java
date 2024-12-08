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
    RebutNode(ButtonLabelManager.rebutNode),
    RebutTelegram(ButtonLabelManager.rebutTelegram),
    RebutTrade(ButtonLabelManager.rebutTrade),
    MainMenu(ButtonLabelManager.mainMenu);

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
