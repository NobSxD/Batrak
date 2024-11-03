package org.example.configuration;

import org.example.button.ButtonLabelManager;
import org.example.entity.enams.state.UserState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.example.entity.enams.state.UserState.*;

@Configuration
public class ButtonRegistration {
    @Bean
    Map<String, UserState> buttonMap() {
        Map<String, UserState> state = new HashMap<>();

        //команды текстом
        state.put(ButtonLabelManager.start, BOT_START);
        state.put(ButtonLabelManager.help, BOT_HELP);
        state.put(ButtonLabelManager.cancel, BOT_CANCEL);
        state.put(ButtonLabelManager.info, INFO_SETTINGS);
        state.put(ButtonLabelManager.main, BOT_MAIN_MENU);
        state.put(ButtonLabelManager.admin, ADMIN);


        //основное меню
        state.put(ButtonLabelManager.infoSettings, INFO_SETTINGS);
        state.put(ButtonLabelManager.deleteAccount, ACCOUNT_LIST);
        state.put(ButtonLabelManager.choiceAccount, ACCOUNT_LIST);
        state.put(ButtonLabelManager.tradeSettings, TRADE_SETTINGS);
        state.put(ButtonLabelManager.tradeOperation, TRADE_OPERATION);
        state.put(ButtonLabelManager.registerAccount, ACCOUNT_ADD_REGISTER);

        //меню настройки трейдинга
        state.put(ButtonLabelManager.namePair, SETTINGS_NAME_PAIR);
        state.put(ButtonLabelManager.amountOrder, SETTINGS_AMOUNT_ORDER);
        state.put(ButtonLabelManager.stepBay, SETTINGS_STEP_BAY);
        state.put(ButtonLabelManager.stepSell, SETTINGS_STEP_SELL);
        state.put(ButtonLabelManager.deposit, SETTINGS_DEPOSIT);
        state.put(ButtonLabelManager.selectStrategy, SETTINGS_NAME_STRATEGY);
        state.put(ButtonLabelManager.enableDemoTrading, SETTINGS_ENABLE_DEMO_TRADING);
        state.put(ButtonLabelManager.mainMenu, BOT_MAIN_MENU);

        //меню управление трейдинга
        state.put(ButtonLabelManager.tradeStart, TRADE_START);
        state.put(ButtonLabelManager.tradeStop, TRADE_STOP);
        state.put(ButtonLabelManager.tradeCancel, TRADE_CANCEL);
        state.put(ButtonLabelManager.main, BOT_MAIN_MENU);

        //выбор стратегии
        state.put(ButtonLabelManager.gridTrading, SETTINGS_SAVE_NAME_STRATEGY);

        //админ меню
        state.put(ButtonLabelManager.listAllUsers, ADMIN_LIST_ALL_USERS);
        state.put(ButtonLabelManager.banUser, ADMIN_BAN_USER);
        state.put(ButtonLabelManager.unbanUser, ADMIN_UNBAN_USER);
        state.put(ButtonLabelManager.tradingStatsAllUsers, ADMIN_TRADING_STATS_ALL_USERS);
        state.put(ButtonLabelManager.tradingStatsSpecificUser, ADMIN_TRADING_STATS_SPECIFIC_USER);
        state.put(ButtonLabelManager.stopTradingAllUsers, ADMIN_STOP_TRADING_ALL_USERS);
        state.put(ButtonLabelManager.grantAdminRights, ADMIN_GRANT_ADMIN_RIGHTS);

        return state;
    }
}
