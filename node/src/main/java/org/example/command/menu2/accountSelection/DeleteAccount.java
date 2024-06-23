package org.example.command.menu2.accountSelection;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAccount implements Command {
	private final Change change;
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		Account account = change.getAccount(text, nodeUser);
		change.deleteFindId(account.getId());
		processServiceCommand.menu2Selection(account.getNameAccount() + "данный аккаунт был удален", nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_DELETE;
	}
}
