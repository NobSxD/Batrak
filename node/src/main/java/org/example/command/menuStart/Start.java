package org.example.command.menuStart;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Start implements Command {
	private final ProcessServiceCommand processServiceCommand;
	private static final Logger logger = LoggerFactory.getLogger(Start.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			processServiceCommand.menu1ChoosingAnExchange( "выбирите биржу", nodeUser.getChatId());
		}catch (Exception e){
			logger.error(e.getMessage());
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_START;
	}



}
