package org.example.command.menuBasic.tradeStatistics;

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
public class MenuStatistics implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String text) {

        try {
            producerTelegramService.menuStatistics("Выбирети пириуд: ", nodeUser.getChatId());
            return "";

        } catch (Exception e) {
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "во время настройки трейдинга произошла ошибка, обратитесь к администратору системы.";
        }
    }

    @Override
    public UserState getType() {
        return UserState.STATISTICS_SELECT;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
