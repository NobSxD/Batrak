package org.example.command.menuBasic.settingsTrade;

import org.example.command.Command;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.menu.MenuStrategy;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsNameStrategy implements Command {
	private final ProducerTelegramService producerTelegramService;
	private final SettingsTradeDAO settingsTradeDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(UserState.SETTINGS_SAVE_NAME_STRATEGY);
		producerTelegramService.strategyMenu("Выберите тарговую стратегию", nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_NAME_STRATEGY;
	}
	@Component
	class SaveNameStrategy implements Command{

		@Override
		public String send(NodeUser nodeUser, String text) {
			try {
				MenuStrategy menuStrategy = MenuStrategy.fromValue(text);
				nodeUser.setState(UserState.TRADE_MANAGER);
				nodeUser.getConfigTrade().setStrategy(menuStrategy);
				producerTelegramService.menuTrade("Выбирети далейшие настройки", nodeUser.getChatId());
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				return "";
			}catch (Exception e){
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Во время выбора стротегии произошла ошибка";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_NAME_STRATEGY;
		}
	}
}
