package org.example.command;

import org.example.entity.NodeUser;

public interface CommandService {
	String send(NodeUser nodeUser, String text);


}
