package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.dto.NodeUserDto;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.example.dao.NodeUserDAO;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUnbanUser implements Command, RoleProvider {
    private final NodeUserDAO nodeUserDAO;
    private final ProducerTelegramService producerTelegramService;
    private final ProcessServiceCommand processServiceCommand;

    @Override
    public String send(NodeUser nodeUser, String nameAccount) {
        List<NodeUser> byIsActiveTrue = nodeUserDAO.findByIsActiveTrue();
        if (byIsActiveTrue.isEmpty()) {
            return "Заблокированных пользователей не найденно";
        }
        List<NodeUserDto> nodeUserDto = byIsActiveTrue.stream()
                .map(user -> new NodeUserDto(user.getId(), user.getChatId(), user.getUsername()))
                .toList();
        producerTelegramService.menuBan(nodeUserDto, "Выберете пользователя что бы его разблакировать", nodeUser.getChatId());
        nodeUser.setState(UserState.U_BAN);
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_UNBAN_USER;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Component
    class UBanUser implements Command, RoleProvider {

        @Override
        public String send(NodeUser nodeUser, String text) {
            Optional<NodeUser> byUsername = nodeUserDAO.findByUsername(text);
            if (byUsername.isEmpty()) {
                return "Пользователь для разблокировки не найден";
            }
            NodeUser user = byUsername.get();
            if (nodeUser.getId().equals(user.getId())) {
                return "Вы не можете разблокировки сам себя";
            }
            user.setIsActive(false);
            nodeUserDAO.save(user);
            processServiceCommand.cancelCache();
            return "Пользователь %s успешно разблокирован".formatted(user.getLastName());
        }

        @Override
        public UserState getType() {
            return UserState.U_BAN;
        }

        @Override
        public Role getRole() {
            return Role.ADMIN;
        }
    }
}
