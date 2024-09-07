package org.example.command;

import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandServiceImpl implements CommandService{
	private final Map<UserState, Command> stateMap;
	private final NodeUserDAO nodeUserDAO;
	
	public CommandServiceImpl(List<Command> commands, NodeUserDAO nodeUserDAO) {
		this.stateMap = commands.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
		this.nodeUserDAO = nodeUserDAO;
	}
	@Override
	public String send(NodeUser nodeUser, String text){
		try {
			Command command = stateMap.get(nodeUser.getState());
			if (command == null){
				log.error("Пользователь id: {} name: {}, ввел неизвестную команду:{}", nodeUser.getId(), nodeUser.getFirstName(), nodeUser.getState());
				return "команда " + nodeUser.getState() + "не найденна.";
			}
			String send = command.send(nodeUser, text);
			nodeUserDAO.save(nodeUser);
			return send;
		}catch (SqlScriptException e){
			log.error("Ошибка при сохронении пользователя id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
			return "";
		}catch (Exception e){
			log.error("Ошибка при сохронении пользователя id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
			return "";
		}
	}



}
