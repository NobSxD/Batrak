package org.example.command.menu2.button;

import org.example.entity.enums.MenuEnums2;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class CancelMenu2Impl implements Menu2 {
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser) {
		nodeUser.setState(BASIC_STATE);
		nodeUserDAO.save(nodeUser);
		return "Команда отменена!";

	}

	@Override
	public MenuEnums2 getType() {
		return MenuEnums2.Cancel;
	}
}
