package org.example.command.menuStart;

import lombok.RequiredArgsConstructor;
import org.example.change.XChangeCommand;
import org.example.command.Command;
import org.example.dao.NodeChangeDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeChange;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@RequiredArgsConstructor
public class SelectionChange implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	private final XChangeCommand xChangeCommand;
	private final NodeChangeDAO nodeChangeDAO;

	@Override
	public String send(NodeUser nodeUser, String nameChange) {
		ChangeType type = ChangeType.fromValue(nameChange);
		NodeChange nodeChange = xChangeCommand.getNodeChange(type);

		Optional<NodeUser> existingUser = nodeChange.getNodeUser().stream()
				.filter(user -> user.getId().equals(nodeUser.getId()))
				.findFirst();
		if (existingUser.isPresent()){

		}else {
			nodeChange.getNodeUser().add(nodeUser);
		}


		nodeUser.setChangeType(type);
		nodeUser.setState(BASIC_STATE);
		nodeUser.setNodeChange(nodeChange);

		nodeChangeDAO.save(nodeChange);
		nodeUserDAO.save(nodeUser);
		processServiceCommand.menu2Selection(nameChange, nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_CHANGE;
	}


}
