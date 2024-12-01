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
public class AdminRebutTelegram implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;
    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            producerTelegramService.producerRebutTelegram("Перезапуск телеграм сервиса", nodeUser.getChatId());
            log.warn("Пользователь {} перезапустил сервис телеграм", nodeUser.getUsername());
        }catch (Exception e){
            producerTelegramService.producerAnswer(e.getMessage(), nodeUser.getChatId());
        }

        return "";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_REBUT_TELEGRAM;
    }

    @Override
    public Role getRole() {
        return Role.SUPERUSER;
    }
}
