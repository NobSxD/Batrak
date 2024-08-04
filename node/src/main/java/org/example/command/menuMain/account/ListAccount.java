package org.example.command.menuMain.account;

import lombok.Data;
import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProducerTelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class ListAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerTelegramService producerTelegramService;
	private final NodeAccount nodeAccount;
	private static final Logger logger = LoggerFactory.getLogger(ListAccount.class);

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			List<Account> accounts = nodeAccount.getAccounts(nodeUser);
			if (accounts.isEmpty()){
				producerTelegramService.mainMenu("У вас нет аккаунта для выбора, сначало добавте аккаунт:", nodeUser.getChatId());
				return "";
			}
			producerTelegramService.accountsMenu(accounts, "Выберите аккаунт", nodeUser.getChatId());
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
