package org.example.command.menuBasic.settingsTrade;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsAmountOrder implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		try{
			nodeUser.setState(UserState.SETTINGS_SAVE_AMOUNT_ORDER);
			return "Укажите сумму ордера";
		}catch (Exception e){
			log.error(e.getMessage());
			return "При переходе в меню, изменение суммы ордера произошла ошибка, обратитесь к администратору";
		}
	}
	@Override
	public UserState getType() {
		return UserState.SETTINGS_AMOUNT_ORDER;
	}
	
}
