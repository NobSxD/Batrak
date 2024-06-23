package org.example.command.menu2.SerringsTrade;


import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NamePair implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	private static Logger logger = Logger.getLogger(NamePair.class);

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_NAME_PAIR);
			nodeUserDAO.save(nodeUser);
			return "Видите название пары, Пример: BTC";
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

				String pair = text + "-USDT";
				new CurrencyPair(pair);//проверка валидности пары

				nodeUser.getSettingsTrade().setNamePair(pair);
				nodeUser.setState(UserState.TRADE_MANAGER);
				settingsTradeDAO.save(nodeUser.getSettingsTrade());
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu3TradeSettings("Валютная пара успешно изменена. Новая пара: " + pair, nodeUser.getChatId());
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
