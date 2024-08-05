package org.example.command.state;

import lombok.Data;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

@Component
@Data
public class InfoAccountImpl implements Command {

	@Override
	public String send(NodeUser nodeUser, String text) {
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_INFO;
	}



}
