package org.example.command.menuMain.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.SettingUpTrading;
import org.example.entity.enams.UserState;
import org.example.service.ProducerTelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeImpl implements Command {
	private final ProducerTelegramService producerTelegramService;
	private final Logger logger = LoggerFactory.getLogger(TradeImpl.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			SettingUpTrading command = SettingUpTrading.fromValue(text);
			if (command == null) {
				producerTelegramService.settingUpTrading("Выбирети далейшие настройки", nodeUser.getChatId());
				return "";
			}

		} catch (Exception e) {
			logger.error(e.getMessage() +  " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
			return "во время настройки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_MANAGER;
	}

	@Component
	class Back implements Command{

		@Override
		public String send(NodeUser nodeUser, String text) {
			producerTelegramService.mainMenu("", nodeUser.getChatId());
			return "";
		}

		@Override
		public UserState getType() {
			return UserState.BOT_BACK;
		}
	}

}
