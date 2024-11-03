package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProcessServiceCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminCancelCache implements Command, RoleProvider {
    private final ProcessServiceCommand processServiceCommand;
    @Override
    public String send(NodeUser nodeUser, String text) {
        processServiceCommand.cancelCache();
        return "Кзш сброшен";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_CANCEL_CACHE;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
