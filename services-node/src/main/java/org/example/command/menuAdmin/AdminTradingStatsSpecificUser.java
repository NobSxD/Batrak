package org.example.command.menuAdmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminTradingStatsSpecificUser implements Command, RoleProvider {
    @Override
    public String send(NodeUser nodeUser, String text) {
        return "AdminTradingStatsSpecificUser";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_TRADING_STATS_SPECIFIC_USER;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
