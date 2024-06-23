package org.example.configuration;

import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.example.entity.enams.UserState.*;

@Configuration
public class ButtonRegistration {
	@Bean
	Map<String, UserState> buttonMap(){
		Map<String, UserState> state = new HashMap<>();
		ChangeType[] changeEnums = ChangeType.values(); //выбор биржи


		state.put("/start", BOT_START);

		for (ChangeType type: changeEnums) {
			state.put(type.toString(), BOT_CHANGE);
		}

		state.put("выбор аккаунта", ACCOUNT_LIST);
		state.put("регистрация", ACCOUNT_ADD_REGISTER);
		state.put("Настрайки трейдинга", TRADE_MANAGER);
		state.put("запуск трейдинга", TRADE_START);
		state.put("остановить трейдинг", TRADE_STOP);
		state.put("отмена", BOT_CANCEL);
		//--------Настройки трейдинга
		state.put("Выбор торговой пары", SETTINGS_NAME_PAIR);
		state.put("Укажите цену ордера", SETTINGS_AMOUNT_ORDER);
		state.put("Глубина размера стакана", SETTINGS_DEPTH_GLASS);
		state.put("В главное мею", BOT_MAIN_MENU);
		state.put("Выбор стратегии", SETTINGS_NAME_STRATEGY);
		state.put("Удаление аккаунта", ACCOUNT_LIST);


		state.put("/cancel", BOT_CANCEL);

		state.put("/help", BOT_HELP);

		return state;
	}
}
