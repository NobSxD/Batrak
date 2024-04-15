package org.example.command.menu2;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class CancelMenu2Impl implements Command {
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser , String text) {
		nodeUser.setState(BASIC_STATE);
		nodeUserDAO.save(nodeUser);
		return "Команда отменена!";

	}

	@Override
	public UserState getType() {
		return UserState.CANCEL;
	}


}
