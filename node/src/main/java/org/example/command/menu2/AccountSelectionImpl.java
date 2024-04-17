package org.example.command.menu2;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceChangeCommands;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountSelectionImpl implements Command {
	private final ProcessServiceChangeCommands processServiceChangeCommands;
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		if (nodeUser.getMenuState() != UserState.ACCOUNT_SELECTION) {
			nodeUser.setMenuState(UserState.ACCOUNT_SELECTION);
			processServiceChangeCommands.ListAccaunts("Выберите аккаунт", nodeUser.getChatId());
			nodeUserDAO.save(nodeUser);
		}else {
			Account changeAccount = processServiceChangeCommands.getChangeAccount(text);
			nodeUser.setAccount(changeAccount);
			nodeUserDAO.save(nodeUser);
			return "Вы выбрали аккаунт " + changeAccount.getNameChange();
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_SELECTION;
	}


}
