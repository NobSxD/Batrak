package org.example.configuration;

import org.example.entity.enams.ChangeType;
import org.example.entity.enams.MainMenu;
import org.example.entity.enams.SettingUpTrading;
import org.example.entity.enams.UserState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ButtonAction {
	@Bean
	Map<String, UserState> buttonMap(){
		Map<String, UserState> state = new HashMap<>();
		ChangeType[] changeEnums = ChangeType.values(); //выбор биржи
		MainMenu[] mainMenus = MainMenu.values(); // главное меню
		SettingUpTrading[] settings = SettingUpTrading.values(); // настройка трейдинга

		for (ChangeType type: changeEnums) {
			state.put(type.toString(),UserState.ACCOUNT_SELECTION);
		}


		return state;
	}
}
