package org.example.command.menu1;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@RequiredArgsConstructor
public class SelectionChange implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;

	@Override
	public String send(NodeUser nodeUser, String nameChange) {
		nodeUser.setChangeType(ChangeType.fromValue(nameChange));
		nodeUser.setState(BASIC_STATE);
		nodeUserDAO.save(nodeUser);
		processServiceCommand.menu2Selection(nameChange, nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_CHANGE;
	}


}
