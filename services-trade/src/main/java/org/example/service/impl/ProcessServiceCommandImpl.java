package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.castom.CustomMessage;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProcessServiceCommandImpl implements ProcessServiceCommand {
	private final ProducerService producerService;

	@Override
	public void sendAnswer(String output, Long chatId) {
		CustomMessage customMessage = CustomMessage.builder()
				.chatId(chatId.toString())
				.text(output)
				.build();
		producerService.producerAnswer(customMessage);
	}


}
