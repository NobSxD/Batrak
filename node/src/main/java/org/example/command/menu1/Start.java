package org.example.command.menu1;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.CHANGE;

@Component
@RequiredArgsConstructor
public class Start implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(CHANGE);
		nodeUserDAO.save(nodeUser);
		processServiceCommand.menu1ChoosingAnExchange(nodeUser.getChatId(), "выбирите биржу");
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.START;
	}



}
