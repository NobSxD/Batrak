package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.example.dao.NodeUserDAO;

import org.springframework.stereotype.Component;

/**
 * Класс TradeStart реализует интерфейс Command и отвечает за запуск процесса трейдинга
 * для пользователя. Он использует сервисы для взаимодействия с внешними системами
 * и базой данных.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStart implements Command, RoleProvider {

    private final ProducerXChangeService producerXChangeService;
    private final NodeUserDAO nodeUserDAO;

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            if (nodeUser.getConfigTrade().getNamePair().equals("-")){
                return "У вас не выбранна валютная пара. Для старта трейдинга выбирите валютную пару.";
            }
            nodeUser = nodeUserDAO.findById(nodeUser.getId()).orElse(nodeUser);
            nodeUser.setLastStartTread(LocalDateTime.now());
            producerXChangeService.startTrade(nodeUser);
            nodeUser.setState(UserState.BASIC_STATE);
            return "";

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "Во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
        }
    }
    @Override
    public UserState getType() {
        return UserState.TRADE_START;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
