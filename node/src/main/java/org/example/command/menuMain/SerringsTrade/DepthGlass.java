package org.example.command.menuMain.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepthGlass implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	private static final Logger logger = Logger.getLogger(DepthGlass.class);

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_DEPTH_GLASS);
			nodeUserDAO.save(nodeUser);
			return "Укажите количество ордеров в стакане";
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				nodeUser.getConfigTrade().setDepthGlass(count);
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu3TradeSettings("Уровень глубины рынка  успешно изменен. Новая глубины рынка: " + text, nodeUser.getChatId());
				return "";
			} catch (NumberFormatException e) {
				logger.error(e.getMessage());
				return "Ведите пожалуйсто целое число";
			} catch (Exception e) {
				logger.error(e.getMessage());
				return "Произошла неожиданная ошибка при изменении глубины рынка, пожалуйста обратитесь к администратору";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_DEPTH_GLASS;
		}
	}
}
