package org.example.command.menuBasic.settingsTrade;

import org.example.command.Command;
import org.example.dao.NodeChangeDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeChange;
import org.example.entity.NodeUser;
import org.example.entity.collect.Pair;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
			if (nodeChange == null){
				return "Биржа не найденна, введите команду /start и выбирети биржу.";
			}
			ArrayList<Pair> pairs = new ArrayList<>(nodeChange.getPairs().values());
			if (!pairs.isEmpty()){
				producerTelegramService.pairMenu(pairs, "Выберите пару", nodeUser.getChatId());
				nodeUser.setState(UserState.SETTINGS_SAVE_NAME_PAIR);
				return "";
			}
			return "Валютной пары не найденно.";
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
				NodeChange nodeChange = nodeChangeDAO.findByMenuChange(nodeUser.getMenuChange()).orElse(null);
				if (nodeChange == null){
					return "Биржа не найденна, введите команду /start и выбирети биржу.";
				}
				Pair pair = nodeChange.getPairs().get(text);
				if (pair == null){
					return "Валютной пары не найденно.";
				}
				nodeUser.getConfigTrade().setNamePair(pair.getNamePair());
				nodeUser.getConfigTrade().setScale(pair.getScale());
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
