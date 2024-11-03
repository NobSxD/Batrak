package org.example.command.menuBasic.tradeSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.button.MessageInfo;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsStepBay implements Command, RoleProvider {
	private final ProducerTelegramService producerTelegramService;


	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(UserState.SETTINGS_SAVE_STEP_BAY);
			return MessageInfo.ENTER_PRICE_STEP;
		} catch (Exception e) {
			log.error(e.getMessage());
			return MessageInfo.PRICE_STEP_MENU_ERROR;
		}
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_STEP_BAY;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}

	@Component
	class SaveDepthGlass implements Command, RoleProvider {

		@Override
		public String send(NodeUser nodeUser, String text) {
			try {
				double count = Double.parseDouble(text);
				nodeUser.setState(UserState.TRADE_MANAGER);
				nodeUser.getConfigTrade().setStepBay(count);
				producerTelegramService.menuTrade(MessageInfo.SELL_PRICE_STEP_CHANGED_BAY
						.formatted(text, "%"), nodeUser.getChatId());
				return "";
			} catch (NumberFormatException e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Ведите пожалуйсто целое число";
			} catch (Exception e) {
				log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
				return "Произошла неожиданная ошибка при изменении глубины рынка, пожалуйста обратитесь к администратору";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_STEP_BAY;
		}

		@Override
		public Role getRole() {
			return Role.USER;
		}
	}
}
