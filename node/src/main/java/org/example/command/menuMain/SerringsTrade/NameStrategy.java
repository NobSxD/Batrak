package org.example.command.menuMain.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NameStrategy implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final static Logger logger = Logger.getLogger(NameStrategy.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(UserState.SETTINGS_SAVE_NAME_STRATEGY);
		processServiceCommand.listStrategy("Выберите тарговую стратегию", nodeUser.getChatId());
		nodeUserDAO.save(nodeUser);
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

				StrategyEnams strategyEnams = StrategyEnams.fromValue(text);
				nodeUser.getConfigTrade().setStrategy(strategyEnams);
				processServiceCommand.menu3TradeSettings("Выбирети далейшие настройки", nodeUser.getChatId());
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				nodeUserDAO.save(nodeUser);
				return "";
			}catch (Exception e){
				logger.error(e.getMessage());
				return "Во время выбора стротегии произошла ошибка";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_NAME_STRATEGY;
		}
	}
}
