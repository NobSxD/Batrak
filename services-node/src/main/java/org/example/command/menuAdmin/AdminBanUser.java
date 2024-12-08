package org.example.command.menuAdmin;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.dto.NodeUserDto;
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
public class AdminBanUser implements Command, RoleProvider {
    private final NodeUserDAO nodeUserDAO;
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String nameAccount) {
        List<NodeUser> byIsActiveFalse = nodeUserDAO.findByIsActiveFalse();
        if (byIsActiveFalse.isEmpty()){
            return "Активных пользователей не найденно";
        }
        List<NodeUserDto> nodeUserDto = byIsActiveFalse.stream()
                .map(user -> new NodeUserDto(user.getId(), user.getChatId(), user.getUsername()))
                .toList();
        producerTelegramService.menuBan(nodeUserDto, "Выберете пользователя что бы его заблокировать", nodeUser.getChatId());
        nodeUser.setState(UserState.BAN);
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.ADMIN_BAN_USER;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Component
    class BanUser implements Command, RoleProvider{

        @Override
        public String send(NodeUser nodeUser, String text) {
            Optional<NodeUser> byUsername = nodeUserDAO.findByUsername(text);
            if (byUsername.isEmpty()){
                return "Пользователь для блокировки не найден";
            }
            NodeUser user = byUsername.get();

            if (nodeUser.getId().equals(user.getId())){
                return "Вы не можете заблокировать сам себя";
            }
            if (user.getRole().equals(Role.SUPERUSER)){
                return "Вы не можете забанить суперюзера";
            }
            if (nodeUser.getRole().equals(Role.ADMIN) && user.getRole().equals(Role.ADMIN)){
                return "Администратор не может заблокировать другового администратора, обратитесь к суперюзеру";
            }
            user.setIsActive(true);
            return "Пользователь %s успешно заблокирован".formatted(user.getLastName());
        }

        @Override
        public UserState getType() {
            return UserState.BAN;
        }

        @Override
        public Role getRole() {
            return Role.ADMIN;
        }
    }
}
