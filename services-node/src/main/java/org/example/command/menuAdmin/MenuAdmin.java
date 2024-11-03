package org.example.command.menuAdmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuAdmin implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            producerTelegramService.menuAdmin("Имя: %s, Роль: %s".formatted(nodeUser.getFirstName(), nodeUser.getRole()),
                    nodeUser.getChatId());
            return "";

        } catch (Exception e) {
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "Во время вызова админ меню произошла ошибка, обратитесь к администратору системы.";
        }
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
