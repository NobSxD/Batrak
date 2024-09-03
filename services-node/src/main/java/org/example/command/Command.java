package org.example.command;

import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.springframework.stereotype.Component;

@Component
public interface Command {
	String send(NodeUser nodeUser, String text);
	UserState getType();


}
