package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStatus implements Command, RoleProvider {
    private final ProducerXChangeService producerXChangeService;
    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            log.info("Пользователь {}, запросил состояние трейдинга", nodeUser.getUsername());
            producerXChangeService.stateTrade(nodeUser);
            nodeUser.setState(UserState.BASIC_STATE);
        } catch (Exception e){
            log.error("Произошла неожиданная ошибка в {}", e.getMessage());
        }
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.TRADE_STATUS;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
