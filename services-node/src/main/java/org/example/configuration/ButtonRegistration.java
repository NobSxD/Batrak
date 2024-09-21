package org.example.configuration;

import org.example.button.ButtonBasic;
import org.example.button.ButtonChange;
import org.example.button.ButtonCommand;
import org.example.button.ButtonMainMenu;
import org.example.button.ButtonOperation;
import org.example.button.ButtonSetting;
import org.example.button.ButtonStrategy;
import org.example.entity.enams.state.UserState;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.example.entity.enams.state.UserState.ACCOUNT_ADD_REGISTER;
import static org.example.entity.enams.state.UserState.ACCOUNT_LIST;
import static org.example.entity.enams.state.UserState.BOT_CANCEL;
import static org.example.entity.enams.state.UserState.BOT_CHANGE;
import static org.example.entity.enams.state.UserState.BOT_HELP;
import static org.example.entity.enams.state.UserState.BOT_MAIN_MENU;
import static org.example.entity.enams.state.UserState.BOT_START;
import static org.example.entity.enams.state.UserState.SETTINGS_ENABLE_DEMO_TRADING;
import static org.example.entity.enams.state.UserState.INFO_SETTINGS;
import static org.example.entity.enams.state.UserState.SETTINGS_AMOUNT_ORDER;
import static org.example.entity.enams.state.UserState.SETTINGS_DEPTH_GLASS;
import static org.example.entity.enams.state.UserState.SETTINGS_NAME_PAIR;
import static org.example.entity.enams.state.UserState.SETTINGS_NAME_STRATEGY;
import static org.example.entity.enams.state.UserState.SETTINGS_SAVE_NAME_STRATEGY;
import static org.example.entity.enams.state.UserState.TRADE_CANCEL;
import static org.example.entity.enams.state.UserState.TRADE_OPERATION;
import static org.example.entity.enams.state.UserState.TRADE_SETTINGS;
import static org.example.entity.enams.state.UserState.TRADE_START;
import static org.example.entity.enams.state.UserState.TRADE_STOP;

@Configuration
public class ButtonRegistration {
	@Bean
	Map<String, UserState> buttonMap(){
		Map<String, UserState> state = new HashMap<>();
		
		//команды текстом
		state.put(ButtonCommand.start,BOT_START);
		state.put(ButtonCommand.help,BOT_HELP);
		state.put(ButtonCommand.cancel, BOT_CANCEL);
		state.put(ButtonCommand.info,INFO_SETTINGS);
		state.put(ButtonCommand.main,BOT_MAIN_MENU);
		
		//выбор биржи
		state.put(ButtonChange.bybit, BOT_CHANGE);
		state.put(ButtonChange.binance, BOT_CHANGE);
		
		//основное меню
		state.put(ButtonBasic.infoSettings, INFO_SETTINGS);
		state.put(ButtonBasic.deleteAccount, ACCOUNT_LIST);
		state.put(ButtonBasic.choiceAccount, ACCOUNT_LIST);
		state.put(ButtonBasic.tradeSettings, TRADE_SETTINGS);
		state.put(ButtonBasic.tradeOperation, TRADE_OPERATION);
		state.put(ButtonBasic.registerAccount, ACCOUNT_ADD_REGISTER);
		
		//меню настройки трейдинга
		state.put(ButtonSetting.namePair, SETTINGS_NAME_PAIR);
		state.put(ButtonSetting.amountOrder, SETTINGS_AMOUNT_ORDER);
		state.put(ButtonSetting.depthGlass, SETTINGS_DEPTH_GLASS);
		state.put(ButtonSetting.selectStrategy, SETTINGS_NAME_STRATEGY);
		state.put(ButtonSetting.enableDemoTrading, SETTINGS_ENABLE_DEMO_TRADING);
		state.put(ButtonMainMenu.mainMenu, BOT_MAIN_MENU);
		
		//меню управление трейдинга
		state.put(ButtonOperation.tradeStart, TRADE_START);
		state.put(ButtonOperation.tradeStop,TRADE_STOP);
		state.put(ButtonOperation.tradeCancel,TRADE_CANCEL);
		state.put(ButtonCommand.main,BOT_MAIN_MENU);
		
		//выбор стратегии
		state.put(ButtonStrategy.slidingProtectiveOrder, SETTINGS_SAVE_NAME_STRATEGY);
		state.put(ButtonStrategy.predictionsOfEvents, SETTINGS_SAVE_NAME_STRATEGY);
		
		return state;
	}
}
