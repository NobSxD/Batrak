package org.example.command.menuBasic.settingsTrade;

import org.example.command.Command;
import org.example.dao.NodeChangeDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeChange;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsNamePair implements Command {
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProducerTelegramService producerTelegramService;
	private final NodeChangeDAO nodeChangeDAO;
	
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			NodeChange nodeChange = nodeChangeDAO.findByMenuChange(nodeUser.getMenuChange()).orElse(null);
			producerTelegramService.pairMenu(nodeChange.getPairs(), "Выберите пару", nodeUser.getChatId());

			nodeUser.setState(UserState.SETTINGS_SAVE_NAME_PAIR);
			return "";
		} catch (Exception e) {
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "При переходе в меню изменение валутной пары произошла ошибка, обратитесь к администратору";
		}

	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_NAME_PAIR;
	}

	@Component
	class SaveNamePair implements Command {

		@Override
		public String send(NodeUser nodeUser, String text) {
			try {
				nodeUser.getConfigTrade().setNamePair(text);
				nodeUser.setState(UserState.TRADE_MANAGER);
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				producerTelegramService.menuTrade("Валютная пара успешно изменена. Новая пара: " + text, nodeUser.getChatId());
				return "";
			} catch (IllegalArgumentException e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Веден не верный формат";
			} catch (Exception e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "При изменении пары произошла неожиданная ошибка";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_NAME_PAIR;
		}
	}

}
