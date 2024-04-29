package org.example.command.menu1;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.CHANGE;

@Component
@RequiredArgsConstructor
public class Start implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(CHANGE);
			nodeUserDAO.save(nodeUser);
			processServiceCommand.menu1ChoosingAnExchange(nodeUser.getChatId(), "выбирите биржу");
		}catch (Exception e){
			LoggerInFile.saveLogInFile(e.getMessage(), "Start.txt");
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.START;
	}



}
