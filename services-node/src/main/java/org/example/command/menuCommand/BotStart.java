package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.dao.NodeChangeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotStart implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;
    private final NodeChangeDAO nodeChangeDAO;

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            nodeUser.setState(UserState.BOT_CHANGE);
            producerTelegramService.menuChange(nodeChangeDAO.findAll(), "Выбирите биржу", nodeUser.getChatId());
        } catch (Exception e) {
            log.error("ошибка: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.BOT_START;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
