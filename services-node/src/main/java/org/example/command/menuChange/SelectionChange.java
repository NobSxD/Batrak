package org.example.command.menuChange;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SelectionChange implements Command {
	private final ProducerTelegramService producerTelegramService;
	
	@Override
	public String send(NodeUser nodeUser, String nameChange) {
		try {
			MenuChange type = MenuChange.fromValue(nameChange);
			if (type == null){
				log.error("Пользователь id: {} name: {}, меню не найденно:{}", nodeUser.getId(), nodeUser.getFirstName(), nameChange);
				return "меню %s не найденна.".formatted(nameChange);
			}
			nodeUser.setMenuChange(type);
			nodeUser.setState(BASIC_STATE);
			producerTelegramService.mainMenu(nameChange, nodeUser.getChatId());
			return "";
		} catch (NullPointerException e){
			log.error("Объект не найден, message: {}", e.getMessage());
			return "Объект не найден";
		} catch (Exception e){
			log.error("Неизвестная ошибка, message: {}", e.getMessage());
			return "Неизвестная ошибка";
		}
	}

	@Override
	public UserState getType() {
		return UserState.BOT_CHANGE;
	}


}
