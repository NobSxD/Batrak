package org.example.command.menuBasic.tradeSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsAmountOrder implements Command, RoleProvider {
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

	@Override
	public Role getRole() {
		return Role.USER;
	}

	@Component
	class SaveAmountOrder implements Command, RoleProvider {

		@Override
		public String send(NodeUser nodeUser, String amount) {
			try {
				nodeUser.getConfigTrade().setAmountOrder(new BigDecimal(amount));
				nodeUser.setState(UserState.TRADE_MANAGER);
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
				return "При изменении суммы ордера произошла неожиданная ошибка";
			}
		}
		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_AMOUNT_ORDER;
		}

		@Override
		public Role getRole() {
			return Role.USER;
		}
	}
}
