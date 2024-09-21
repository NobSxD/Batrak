package org.example.command.menuBasic.tradeSettings;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.menu.MenuSetting;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuTrade implements Command {
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			MenuSetting command = MenuSetting.fromValue(text);
			if (command == null) {
				producerTelegramService.menuTrade("Выбирети далейшие настройки", nodeUser.getChatId());
				return "";
			}
		} catch (Exception e) {
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время настройки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_SETTINGS;
	}
	

}
