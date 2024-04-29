package org.example.command.menu2;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Data
public class AccountSelectionImpl implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {

			if (nodeUser.getMenuState() != UserState.ACCOUNT_SELECTION) {
				nodeUser.setMenuState(UserState.ACCOUNT_SELECTION);
				var sendMessage = new SendMessage();
				sendMessage.setChatId(nodeUser.getChatId());
				sendMessage.setText("Выберите аккаунт");
				producerService.producerAccountButton(nodeUser.getChange().getAccounts(), sendMessage);
				nodeUserDAO.save(nodeUser);
			} else {
				Account changeAccount = nodeUser.getChange().newAccount();
				nodeUser.setMenuState(null);
				nodeUser.setAccount(changeAccount);
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu2Selection("Вы выбрали аккаунт " + changeAccount.getNameChange(), nodeUser.getChatId());
			}
		} catch (Exception e){
			LoggerInFile.saveLogInFile(e.getMessage(), "AccountSelectionImpl.txt");
			return "во время выбора аккаунта произошла ошибка, обратитесь к администратору системы.";
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_SELECTION;
	}


}
