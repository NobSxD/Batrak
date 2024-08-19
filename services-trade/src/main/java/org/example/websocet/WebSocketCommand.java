package org.example.websocet;

import org.example.entity.NodeUser;

public interface WebSocketCommand {
	WebSocketChange webSocketChange(NodeUser nodeUser);
	
}
