package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInfoSettings implements Command {

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            if (nodeUser == null) {
                return "Пользователь не найден, введите команду /start";
            }
            if (nodeUser.getMenuChange() == null) {
                return "Биржа не найдена, введите команду /start";
            }
            String nameChange = nodeUser.getMenuChange().name();
            BigDecimal amountOrder = nodeUser.getConfigTrade().getAmountOrder();
            String pair = nodeUser.getConfigTrade().getNamePair();
            String accountName = Optional.ofNullable(nodeUser)
                    .map(NodeUser::getAccount)
                    .map(Account::getNameAccount)
                    .orElse("Аккаунт не выбран");

            int depthGlass = nodeUser.getConfigTrade().getDepthGlass();
            StringBuilder settings = new StringBuilder()
                    .append("<--[Текущие настройки]--> \n")
                    .append("Текущий аккаунт: ").append(accountName).append(".\n")
                    .append("Биржа: ").append(nameChange).append(".\n")
                    .append("Сумма ордера: $").append(amountOrder).append(".\n")
                    .append("Валютная пара: ").append(pair).append(".\n")
                    .append("Глубина ордеров в стакане: ").append(depthGlass).append(".\n");
            return settings.toString();
        } catch (NullPointerException e) {
            log.error("Пользовтель: {}. id: {}. Ошибка: NPE {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "Биржа не найдена, выбирете биржу /start и повторите попытку";
        } catch (Exception e) {
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "Во время запроса данных о ваших настройках произошла неизвестная ошибка " + e.getMessage();
        }
    }

    @Override
    public UserState getType() {
        return UserState.INFO_SETTINGS;
    }
}
