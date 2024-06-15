package org.example.command.menu2;

import lombok.Data;
import org.example.change.Change;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProducerService;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Data
public class AccountSelectionImpl implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	private final Change change;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
				var sendMessage = new SendMessage();
				sendMessage.setChatId(nodeUser.getChatId());
				sendMessage.setText("Выберите аккаунт");
				producerService.producerAccountButton(change.getAccounts(nodeUser), sendMessage);
				nodeUser.setState(UserState.SELECT);
				nodeUserDAO.save(nodeUser);
		} catch (Exception e){
			System.out.println(e.getMessage());

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
