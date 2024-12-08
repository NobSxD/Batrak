package org.example.command.menuBasic.tradeSettings;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.BASIC_STATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuTrade implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            producerTelegramService.menuTrade("Выбирети далейшие настройки", nodeUser.getChatId());
            nodeUser.setState(BASIC_STATE);
            return "";

        } catch (Exception e) {
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "во время настройки трейдинга произошла ошибка, обратитесь к администратору системы.";
        }
    }

    @Override
    public UserState getType() {
        return UserState.TRADE_SETTINGS;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
