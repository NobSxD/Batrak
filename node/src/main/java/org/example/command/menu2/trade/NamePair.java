package org.example.command.menu2.trade;


import lombok.RequiredArgsConstructor;
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
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			new CurrencyPair(text  + "-USDT");//проверка валидности пары

			nodeUser.getSettingsTrade().setNamePair(text +"-USDT");
			nodeUser.setState(UserState.MANAGER_TRADE);
			settingsTradeDAO.save(nodeUser.getSettingsTrade());
			nodeUserDAO.save(nodeUser);
			processServiceCommand.menu3TradeSettings("Валютная пара успешно изменена. Новая пара: " + text, nodeUser.getChatId());
			return "";

		}catch (Exception e){
			System.out.println(e.getMessage());
			return "Веден не верный формат";
		}

	}

	@Override
	public UserState getType() {
		return UserState.NAME_PAIR;
	}
}
