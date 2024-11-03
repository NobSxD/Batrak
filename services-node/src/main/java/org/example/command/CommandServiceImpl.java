package org.example.command;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.AccessControl;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.button.MessageInfo.*;

/**
 * **CommandServiceImpl**.
 * <p>
 * Этот класс реализует паттерн Командер с использованием Spring. Spring автоматически загружает в конструктор список всех
 * команд и преобразует его в карту (мапу), что позволяет быстро и удобно получать нужную команду по ключу.
 * Это избавляет от необходимости использования множественных условий (if-else или switch-case) для выбора нужного класса команды.
 */
@Service
@Slf4j
public class CommandServiceImpl implements CommandService {
    private final Map<UserState, Command> stateMap;
    private final NodeUserDAO nodeUserDAO;
    private final AccessControl accessControl;

    /**
     * Конструктор для CommandServiceImpl.
     *
     * @param commands      Список всех команд.
     * @param nodeUserDAO   DAO объект для пользователя
     * @param accessControl Объект для контроля доступа к админ панели
     */
    public CommandServiceImpl(List<Command> commands, NodeUserDAO nodeUserDAO, AccessControl accessControl) {
        this.stateMap = commands.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
        this.nodeUserDAO = nodeUserDAO;
        this.accessControl = accessControl;
    }

    /**
     * Метод для отправки команды.
     *
     * @param nodeUser Пользователь, отправляющий команду.
     * @param text     Текст команды.
     * @return Ответ на команду.
     */
    @Override
    public String send(NodeUser nodeUser, String text) {
        Command command = stateMap.get(nodeUser.getState());
        if (command == null) {
            log.error("Пользователь id: {} name: {}, ввел неизвестную команду: {}", nodeUser.getId(), nodeUser.getFirstName(), nodeUser.getState());
            return String.format(UNKNOWN_COMMAND_MESSAGE_TEMPLATE, nodeUser.getState());
        }

        boolean hasAccess = accessControl.hasAccess(command, nodeUser.getRole());
        if (!hasAccess) {
            log.warn("Пользователь: {}, с ролью: {}, пытался получить доступ до класса: {}",
                    nodeUser.getFirstName(), nodeUser.getRole(), command.getClass());
            return NO_ACCESS_MESSAGE;
        }

        try {
            nodeUserDAO.save(nodeUser);
            return command.send(nodeUser, text);
        } catch (IllegalArgumentException e) {
            log.error("На сервере не добавлено секретное слово id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
            return ENCRYPTION_ERROR_MESSAGE;
        } catch (SqlScriptException e) {
            log.error("Ошибка при сохранении пользователя id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
            return DATABASE_SAVE_ERROR_MESSAGE;
        } catch (Exception e) {
            log.error("Неизвестная ошибка id: {}, ошибка: {}", nodeUser.getId(), e.getMessage());
            return e.getMessage();
        }
    }


}
