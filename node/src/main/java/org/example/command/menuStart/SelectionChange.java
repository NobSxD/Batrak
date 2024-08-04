package org.example.command.menuStart;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@RequiredArgsConstructor
public class SelectionChange implements Command {
	private final ProducerTelegramService producerTelegramService;
	private final NodeUserDAO nodeUserDAO;


	@Override
	public String send(NodeUser nodeUser, String nameChange) {
		try {
			ChangeType type = ChangeType.fromValue(nameChange);
			nodeUser.setChangeType(type);
			nodeUser.setState(BASIC_STATE);
			nodeUserDAO.save(nodeUser);
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
