package org.example.command.menu2.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TradeAmountOrder implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try{
			double amount = Double.parseDouble(text);
			BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);
			nodeUser.getSettingsTrade().setAmountOrder(amountBigDecimal);
			settingsTradeDAO.save(nodeUser.getSettingsTrade());
			nodeUserDAO.save(nodeUser);
			processServiceCommand.menu3TradeSettings("Цена ордера успешно изменена. Новая цена: " + text, nodeUser.getChatId());
			return "";
		}catch (Exception e){
			System.out.println(e.getMessage());
			return "Ведите число";
		}
	}

	@Override
	public UserState getType() {
		return UserState.AMOUNT_ORDER;
	}
}
