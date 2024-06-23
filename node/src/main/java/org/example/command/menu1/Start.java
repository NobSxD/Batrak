package org.example.command.menu1;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Start implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private static final Logger logger = Logger.getLogger(Start.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			processServiceCommand.menu1ChoosingAnExchange( "выбирите биржу", nodeUser.getChatId());
		}catch (Exception e){
			logger.error(e.getMessage());
			LoggerInFile.saveLogInFile(e.getMessage(), "Start.txt");
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_START;
	}



}
