package org.example.command.menuMain.account;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.change.Change;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.Account;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAccount implements Command {
	private final Change change;
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	private final Logger logger = Logger.getLogger(DeleteAccount.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			Account account = change.getAccount(text, nodeUser);
			change.deleteFindId(account.getId());
			if (nodeUser.getNodeChange().getAccount().getNameAccount().equals(text)){
				nodeUser.getNodeChange().setAccount(null);
				nodeUserDAO.save(nodeUser);
			}
			processServiceCommand.menu2Selection(account.getNameAccount() + " данный аккаунт был удален ", nodeUser.getChatId());
			return "";
		} catch (NullPointerException e){
			logger.error(e.getMessage() + "аккаунт пользователя под id " + nodeUser.getChatId() + " не найден. Аккаунт имя " + text);
			return "Аккаунт " + text + " не найден";
		}catch (Exception e){
			logger.error(e.getMessage() + "Не известная ошибка в классе DeleteAccount");
			return "Произошла непредвиденная ошибка";
		}
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_DELETE;
	}
}
