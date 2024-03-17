package org.example.command.menu2.impl;

import org.example.command.menu2.Menu2;
import org.example.entity.enums.MenuEnums2;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.ACCOUNT_NAME;


@Component
@Data
public class RegisterAccountImpl implements Menu2 {
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser) {
		nodeUser.setState(ACCOUNT_NAME);
		nodeUserDAO.save(nodeUser);
		return "Ведите имя аккаунта";
	}

	@Override
	public MenuEnums2 getType() {
		return MenuEnums2.RegisterAccount;
	}
}
