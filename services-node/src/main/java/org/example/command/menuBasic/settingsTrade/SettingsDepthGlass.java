package org.example.command.menuBasic.settingsTrade;

import org.example.command.Command;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsDepthGlass implements Command {
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProducerTelegramService producerTelegramService;


	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_DEPTH_GLASS);
			return "Укажите количество ордеров в стакане";
		} catch (Exception e) {
			log.error(e.getMessage());
			return "При переходе в меню изменение глубины рынка произошла ошибка, обратитесь к администратору";
		}
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_DEPTH_GLASS;
	}

	@Component
	class SaveDepthGlass implements Command {

		@Override
		public String send(NodeUser nodeUser, String text) {
			try {
				int count = Integer.parseInt(text);
				nodeUser.setState(UserState.TRADE_MANAGER);
				nodeUser.getConfigTrade().setDepthGlass(count);
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				producerTelegramService.menuTrade("Уровень глубины рынка  успешно изменен. Новая глубины рынка: " + text, nodeUser.getChatId());
				return "";
			} catch (NumberFormatException e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Ведите пожалуйсто целое число";
			} catch (Exception e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Произошла неожиданная ошибка при изменении глубины рынка, пожалуйста обратитесь к администратору";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_DEPTH_GLASS;
		}
	}
}
