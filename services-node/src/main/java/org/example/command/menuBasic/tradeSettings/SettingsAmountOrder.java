package org.example.command.menuBasic.tradeSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsAmountOrder implements Command {
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_AMOUNT_ORDER);
			return "Укажите сумму ордера";
		} catch (Exception e) {
			log.error(e.getMessage());
			return "При переходе в меню, изменение суммы ордера произошла ошибка, обратитесь к администратору";
		}
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_AMOUNT_ORDER;
	}

	@Component
	class SaveAmountOrder implements Command {

		@Override
		public String send(NodeUser nodeUser, String amount) {
			try {
				nodeUser.getConfigTrade().setAmountOrder(new BigDecimal(amount));
				nodeUser.setState(UserState.TRADE_MANAGER);
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				producerTelegramService.menuTrade(String.format("Сумма ордера успешно изменена. Новая сумма ордера: $%s", amount), nodeUser.getChatId());
				return "";
			}catch (NumberFormatException e){
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Ведите пожалуйсто целое число";
			}
			catch (IllegalArgumentException e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
				return "Веден не верный формат";
			} catch (Exception e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
				return "При изменении пары произошла неожиданная ошибка";
			}
		}
		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_AMOUNT_ORDER;
		}
	}
}
