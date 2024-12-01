package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminRebutTrade implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;
    @Override
    public String send(NodeUser nodeUser, String text) {

        return null;
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_REBUT_TRADE;
    }

    @Override
    public Role getRole() {
        return Role.SUPERUSER;
    }
}
