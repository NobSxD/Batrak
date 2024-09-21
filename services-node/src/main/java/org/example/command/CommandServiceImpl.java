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

/**
 * **CommandServiceImpl**.
 *
 * Этот класс реализует паттерн Командер с использованием Spring. Spring автоматически загружает в конструктор список всех
 * команд и преобразует его в карту (мапу), что позволяет быстро и удобно получать нужную команду по ключу.
 * Это избавляет от необходимости использования множественных условий (if-else или switch-case) для выбора нужного класса команды.
 */
@Service
@Slf4j
public class CommandServiceImpl implements CommandService{
	private final Map<UserState, Command> stateMap;
	private final NodeUserDAO nodeUserDAO;

	/**
	 * Конструктор для CommandServiceImpl.
	 *
	 * @param commands Список всех команд.
	 * @param nodeUserDAO DAO объект для пользователя
	 */
	public CommandServiceImpl(List<Command> commands, NodeUserDAO nodeUserDAO) {
		this.stateMap = commands.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
		this.nodeUserDAO = nodeUserDAO;
	}

	/**
	 * Метод для отправки команды.
	 *
	 * @param nodeUser Пользователь, отправляющий команду.
	 * @param text Текст команды.
	 * @return Ответ на команду.
	 */
	@Override
	public String send(NodeUser nodeUser, String text){
		Command command;
		String send;
		try {
			if (nodeUser == null){
				log.error("Пользователь = null");
				return "Мы не смогли найти вас в системе, введите команду /start и попробуйте снова.";
			}
			command = stateMap.get(nodeUser.getState());
			if (command == null){
				log.error("Пользователь id: {} name: {}, ввел неизвестную команду:{}", nodeUser.getId(), nodeUser.getFirstName(), nodeUser.getState());
				return "команда %s не найденна.".formatted(nodeUser.getState());
			}
			send = command.send(nodeUser, text);
			nodeUserDAO.save(nodeUser);
			return send;
		}catch (SqlScriptException e){
			log.error("Ошибка при сохронении пользователя id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
			return "";
		}catch (Exception e){
			log.error("Неизвестная ошибка id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
			return "";
		}
	}



}
