package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotStart implements Command {
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			producerTelegramService.changeMenu( "Выбирите биржу", nodeUser.getChatId());
		}catch (Exception e){
			log.error("ошибка: {}",e.getMessage());
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_START;
	}



}
