package org.example.command.menuChange;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.collect.ChangeType;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.BASIC_STATE;


@Component
@RequiredArgsConstructor
@Slf4j
public class SelectionChange implements Command, RoleProvider {
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String nameChange) {
        try {
            ChangeType type = ChangeType.fromValue(nameChange);

            String message = checkAccountType(nodeUser, nameChange, type);
            nodeUser.setChangeType(type);
            nodeUser.setState(BASIC_STATE);
            producerTelegramService.menuMain(nameChange, nodeUser.getChatId());
            return message;
        } catch (NullPointerException e) {
            log.error("Объект не найден, message: {}", e.getMessage());
            return "Объект не найден";
        } catch (Exception e) {
            log.error("Неизвестная ошибка, message: {}", e.getMessage());
            return "Неизвестная ошибка";
        }
    }

    private String checkAccountType(NodeUser nodeUser, String nameChange, ChangeType type) {
        if (type == null) {
            log.error("Пользователь id: {} name: {}, меню не найденно:{}",
                    nodeUser.getId(), nodeUser.getFirstName(), nameChange);
            return "Биржа %s не найденна.".formatted(nameChange);
        }

        Account account = nodeUser.getAccount();
        if (account == null) {
            return "";
        }
        if (!account.getChangeType().equals(type)) {
            nodeUser.setAccount(null);
            return """
                    Уважаемый пользователь, при выборе биржи %s у вас был сохранен аккаунт типа %s.
                    По этому выбранный аккаунт не был выбран, пожалуйста выберете другой аккаунт.
                    """
                    .formatted(type, account.getChangeType());
        }
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.BOT_CHANGE;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
