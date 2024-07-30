package org.example.command.menuMain.SerringsTrade;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.command.Command;
import org.example.dao.NodeChangeDAO;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeChange;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NamePair implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	private final NodeChangeDAO nodeChangeDAO;
	private static Logger logger = LoggerFactory.getLogger(NamePair.class);

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {

			NodeChange nodeChange = nodeChangeDAO.findByChangeType(nodeUser.getChangeType()).orElse(null);
			processServiceCommand.listPair(nodeChange.getPairs(), "Выберите пару", nodeUser.getChatId());

			nodeUser.setState(UserState.SETTINGS_SAVE_NAME_PAIR);
			nodeUserDAO.save(nodeUser);
			return "";
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu3TradeSettings("Валютная пара успешно изменена. Новая пара: " + text, nodeUser.getChatId());
				return "";
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage());
				return "Веден не верный формат";
			} catch (Exception e) {
				logger.error(e.getMessage());
				return "При изменении пары произошла неожиданная ошибка";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_NAME_PAIR;
		}
	}

}
