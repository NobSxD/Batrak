package org.example.command.menuBasic.tradeSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.button.MessageInfo;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsDeposit implements Command {
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_AMOUNT_ORDER);
			return MessageInfo.ENTER_DEPOSIT_AMOUNT;
		} catch (Exception e) {
			log.error(e.getMessage());
			return MessageInfo.DEPOSIT_MENU_ERROR;
		}
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_DEPOSIT;
	}

	@Component
	class SaveAmountOrder implements Command {

		@Override
		public String send(NodeUser nodeUser, String amount) {
			try {
				nodeUser.getConfigTrade().setAmountOrder(new BigDecimal(amount));
				nodeUser.setState(UserState.TRADE_MANAGER);
				producerTelegramService.menuTrade(String.format(MessageInfo.DEPOSIT_AMOUNT_CHANGED, amount), nodeUser.getChatId());
				return "";
			}catch (NumberFormatException e){
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return MessageInfo.NUMBER_FORMAT_EXCEPTION;
			}
			catch (IllegalArgumentException e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
				return "Веден не верный формат";
			} catch (Exception e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
				return "При изменении депозита произошла неожиданная ошибка";
			}
		}
		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_DEPOSIT;
		}
	}
}
