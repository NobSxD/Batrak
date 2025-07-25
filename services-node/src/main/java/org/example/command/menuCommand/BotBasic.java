package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BotBasic implements Command, RoleProvider {
    @Override
    public String send(NodeUser nodeUser, String text) {
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.BASIC_STATE;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
