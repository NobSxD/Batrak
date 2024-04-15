package org.example.command;

import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService{
	private final Map<UserState, Command> stateMap;



	public CommandServiceImpl(List<Command> commands, List<Command> commands2) {
		this.stateMap = commands.stream().collect(Collectors.toMap(Command::getType, Function.identity()));

	}
	@Override
	public String send(NodeUser nodeUser, String text){
		Command command = stateMap.get(nodeUser.getState());
		return command.send(nodeUser, text);
	}



}
