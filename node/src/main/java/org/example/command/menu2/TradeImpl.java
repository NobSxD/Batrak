package org.example.command.menu2;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
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
	private final NodeUserDAO nodeUserDAO;

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			SettingUpTrading command = SettingUpTrading.fromValue(text);
			if (command == null) {
				processServiceCommand.menu3TradeSettings("Выбирети далейшие настройки", nodeUser.getChatId());
				return "";
			}

			if (command.equals(SettingUpTrading.AmountOrder)) {
				nodeUser.setState(UserState.AMOUNT_ORDER);
				nodeUserDAO.save(nodeUser);
				return "Укажите цену ордера";
			}
			if (command.equals(SettingUpTrading.DepthGlass)) {
				nodeUser.setState(UserState.DEPTH_GLASS);
				nodeUserDAO.save(nodeUser);
				return "Укажите количество ордеров в стакане";
			}
			if (command.equals(SettingUpTrading.NamePair)) {
				nodeUser.setState(UserState.NAME_PAIR);
				nodeUserDAO.save(nodeUser);
				return "Видите название пары, Пример: BTC";
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
		return UserState.MANAGER_TRADE;
	}

}
