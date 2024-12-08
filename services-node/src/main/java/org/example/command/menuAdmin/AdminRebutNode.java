package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminRebutNode implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;
    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            producerTelegramService.producerAnswer("Сервис node будет перезапущен", nodeUser.getChatId());
            log.warn("Сервис был перезапущен пользователем: {}", nodeUser.getUsername());
            System.exit(0);
        } catch (Exception e){
            return e.getMessage();
        }
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_REBUT_NODE;
    }

    @Override
    public Role getRole() {
        return Role.SUPERUSER;
    }
}
