package org.example.command.state;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;

@Component
@Data
public class BasicStateImpl implements Command {
	private final ProducerService producerService;

	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;

	@Override
	public String send(NodeUser nodeUser, String text) {
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BASIC_STATE;
	}





}
