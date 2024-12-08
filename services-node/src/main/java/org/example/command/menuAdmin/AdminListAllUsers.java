package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.example.dao.NodeUserDAO;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminListAllUsers implements Command, RoleProvider {
    private final NodeUserDAO nodeUserDAO;

    @Override
    public String send(NodeUser nodeUser, String text) {
        List<NodeUser> all = nodeUserDAO.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (all.isEmpty()){
            return "Пользователей не найденно для возвращение в админ меню нажмите /admin \n "
                    + "для возвращение в главное меню нажмите /main";
        }

        return all.stream()
                .map(user -> """
                1. id: %s
                2. Name: %s %s
                3. NikeName: %s
                4. UserState: %s
                5. Role: %s
                6. Last_tread: %s
                """.formatted(
                        user.getId(),
                        user.getLastName(),
                        user.getFirstName(),
                        user.getUsername(),
                        user.getState(),
                        user.getRole(),
                        user.getLastStartTread() != null
                                ? user.getLastStartTread().format(formatter)
                                : "null"
                ))
                .collect(Collectors.joining("\n\n")) + "\n---";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_LIST_ALL_USERS;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
