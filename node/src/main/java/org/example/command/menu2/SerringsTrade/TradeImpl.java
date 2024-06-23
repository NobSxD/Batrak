package org.example.command.menu2.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.SettingUpTrading;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeImpl implements Command {
	private final ProcessServiceCommand processServiceCommand;

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			SettingUpTrading command = SettingUpTrading.fromValue(text);
			if (command == null) {
				processServiceCommand.menu3TradeSettings("Выбирети далейшие настройки", nodeUser.getChatId());
				return "";
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			LoggerInFile.saveLogInFile(e.getMessage(), "TradeImpl");
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
			processServiceCommand.menu2Selection("", nodeUser.getChatId());
			return "";
		}

		@Override
		public UserState getType() {
			return UserState.BOT_BACK;
		}
	}

}
