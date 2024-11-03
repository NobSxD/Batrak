package org.example.command.menuCommand;

import lombok.Data;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.springframework.stereotype.Component;

@Component
@Data
public class BotHelp implements Command, RoleProvider {


    @Override
    public String send(NodeUser nodeUser, String text) {
        return """
                Выбирите действие:
                /start - выбор биржи
                /main - вызвать главное меню
                /info_settings - посмотреть текущие настройки
                /cancel - отмена выполнения текущей команды;
                """;
        //TODO добавить сайт
    }

    @Override
    public UserState getType() {
        return UserState.BOT_HELP;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
