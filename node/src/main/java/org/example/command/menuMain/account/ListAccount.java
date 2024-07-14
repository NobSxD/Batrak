package org.example.command.menuMain.account;

import lombok.Data;
import org.apache.log4j.Logger;
import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class ListAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final ProcessServiceCommand processServiceCommand;
	private final NodeAccount nodeAccount;
	private static final Logger logger = Logger.getLogger(ListAccount.class);

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			List<Account> accounts = nodeAccount.getAccounts(nodeUser);
			if (accounts.isEmpty()){
				processServiceCommand.menu2Selection("У вас нет аккаунта для выбора, сначало добавте аккаунт:", nodeUser.getChatId());
				return "";
			}
			processServiceCommand.listAccount(accounts, "Выберите аккаунт", nodeUser.getChatId());
			if (text.equals("Удаление аккаунта")){
				nodeUser.setState(UserState.ACCOUNT_DELETE);
			} else {
				nodeUser.setState(UserState.ACCOUNT_SELECT);
			}
			nodeUserDAO.save(nodeUser);
			return "";
		} catch (Exception e) {
			logger.error(e.getMessage() +  " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
			return "во время выбора аккаунта произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_LIST;
	}

}
