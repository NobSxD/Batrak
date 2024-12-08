package org.example.configuration;

import org.example.button.ButtonLabelManager;

import java.util.HashMap;
import java.util.Map;

import org.example.entity.enams.state.UserState;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.entity.enams.state.UserState.ACCOUNT_ADD_REGISTER;
import static org.example.entity.enams.state.UserState.ACCOUNT_LIST;
import static org.example.entity.enams.state.UserState.ADMIN;
import static org.example.entity.enams.state.UserState.ADMIN_BAN_USER;
import static org.example.entity.enams.state.UserState.ADMIN_GRANT_ADMIN_RIGHTS;
import static org.example.entity.enams.state.UserState.ADMIN_LIST_ALL_USERS;
import static org.example.entity.enams.state.UserState.ADMIN_REBUT_NODE;
import static org.example.entity.enams.state.UserState.ADMIN_REBUT_TELEGRAM;
import static org.example.entity.enams.state.UserState.ADMIN_REBUT_TRADE;
import static org.example.entity.enams.state.UserState.ADMIN_STOP_TRADING_ALL_USERS;
import static org.example.entity.enams.state.UserState.ADMIN_TRADING_STATS_ALL_USERS;
import static org.example.entity.enams.state.UserState.ADMIN_TRADING_STATS_SPECIFIC_USER;
import static org.example.entity.enams.state.UserState.ADMIN_UNBAN_USER;
import static org.example.entity.enams.state.UserState.BOT_CANCEL;
import static org.example.entity.enams.state.UserState.BOT_HELP;
import static org.example.entity.enams.state.UserState.BOT_MAIN_MENU;
import static org.example.entity.enams.state.UserState.BOT_START;
import static org.example.entity.enams.state.UserState.INFO_SETTINGS;
import static org.example.entity.enams.state.UserState.SETTINGS_AMOUNT_ORDER;
import static org.example.entity.enams.state.UserState.SETTINGS_DEPOSIT;
import static org.example.entity.enams.state.UserState.SETTINGS_ENABLE_DEMO_TRADING;
import static org.example.entity.enams.state.UserState.SETTINGS_NAME_PAIR;
import static org.example.entity.enams.state.UserState.SETTINGS_NAME_STRATEGY;
import static org.example.entity.enams.state.UserState.SETTINGS_SAVE_NAME_STRATEGY;
import static org.example.entity.enams.state.UserState.SETTINGS_STEP_BAY;
import static org.example.entity.enams.state.UserState.SETTINGS_STEP_SELL;
import static org.example.entity.enams.state.UserState.STATISTICS_HALF_YEAR;
import static org.example.entity.enams.state.UserState.STATISTICS_MONTH;
import static org.example.entity.enams.state.UserState.STATISTICS_SELECT;
import static org.example.entity.enams.state.UserState.STATISTICS_TODAY;
import static org.example.entity.enams.state.UserState.STATISTICS_WEEK;
import static org.example.entity.enams.state.UserState.STATISTICS_YEAR;
import static org.example.entity.enams.state.UserState.TRADE_CANCEL;
import static org.example.entity.enams.state.UserState.TRADE_OPERATION;
import static org.example.entity.enams.state.UserState.TRADE_SETTINGS;
import static org.example.entity.enams.state.UserState.TRADE_START;
import static org.example.entity.enams.state.UserState.TRADE_STATUS;
import static org.example.entity.enams.state.UserState.TRADE_STOP;

@Configuration
public class ButtonRegistration {
    @Bean
    public Map<String, UserState> buttonMap() {
        Map<String, UserState> state = new HashMap<>();

        //команды текстом
        state.put(ButtonLabelManager.start, BOT_START);
        state.put(ButtonLabelManager.help, BOT_HELP);
        state.put(ButtonLabelManager.cancel, BOT_CANCEL);
        state.put(ButtonLabelManager.info, INFO_SETTINGS);
        state.put(ButtonLabelManager.main, BOT_MAIN_MENU);
        state.put(ButtonLabelManager.admin, ADMIN);
        state.put(ButtonLabelManager.stateTrade, TRADE_STATUS);

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
        state.put(ButtonLabelManager.tradeStatus, TRADE_STATUS);
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
        state.put(ButtonLabelManager.rebutNode, ADMIN_REBUT_NODE);
        state.put(ButtonLabelManager.rebutTelegram, ADMIN_REBUT_TELEGRAM);
        state.put(ButtonLabelManager.rebutTrade, ADMIN_REBUT_TRADE);

        //меню статистики
        state.put(ButtonLabelManager.statistics, STATISTICS_SELECT);
        state.put(ButtonLabelManager.today, STATISTICS_TODAY);
        state.put(ButtonLabelManager.thisOfWeek, STATISTICS_WEEK);
        state.put(ButtonLabelManager.thisOfMonth, STATISTICS_MONTH);
        state.put(ButtonLabelManager.thisHalfYear, STATISTICS_HALF_YEAR);
        state.put(ButtonLabelManager.thisYear, STATISTICS_YEAR);

        return state;
    }
}
