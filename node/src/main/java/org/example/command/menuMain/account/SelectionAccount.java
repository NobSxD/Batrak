package org.example.command.menuMain.account;

import lombok.RequiredArgsConstructor;
import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.Account;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelectionAccount implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	private final NodeAccount nodeAccount;

	@Override
	public String send(NodeUser nodeUser, String text) {

		try {
			Account changeAccount = nodeAccount.getAccount(text, nodeUser);
			nodeAccount.saveAccount(changeAccount, nodeUser);
			nodeUser.setAccount(changeAccount);
			nodeUserDAO.save(nodeUser);
			processServiceCommand.menu2Selection("Вы выбрали аккаунт " + changeAccount.getNameAccount(), nodeUser.getChatId());
		} catch (Exception e){
			System.out.println(e.getMessage());
			return "Не получилось выбрать аккаунт";
		}

		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_SELECT;
	}
}
