package org.example.websocet;

import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WebSocketCommandImpl implements WebSocketCommand {


	private final Map<ChangeType, WebSocketChange> stateMap;


	public WebSocketCommandImpl(List<WebSocketChange> commands) {
		this.stateMap = commands.stream().collect(Collectors.toMap(WebSocketChange::getType, Function.identity()));

	}
	
	@Override
	public WebSocketChange webSocketChange(NodeUser nodeUser) {
		return stateMap.get(nodeUser.getChangeType());
	}
}
