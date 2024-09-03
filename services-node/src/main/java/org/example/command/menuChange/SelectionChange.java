package org.example.command.menuChange;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.menu.MenuChange;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.BASIC_STATE;


@Component
@RequiredArgsConstructor
public class SelectionChange implements Command {
	private final ProducerTelegramService producerTelegramService;
	
	@Override
	public String send(NodeUser nodeUser, String nameChange) {
		try {
			MenuChange type = MenuChange.fromValue(nameChange);
			nodeUser.setMenuChange(type);
			nodeUser.setState(BASIC_STATE);
			producerTelegramService.mainMenu(nameChange, nodeUser.getChatId());
			return "";
		} catch (NullPointerException e){
			return "Объект не найден";
		} catch (Exception e){
			return "Неизвестная ошибка";
		}
	}

	@Override
	public UserState getType() {
		return UserState.BOT_CHANGE;
	}


}
