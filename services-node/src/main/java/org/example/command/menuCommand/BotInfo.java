package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInfo implements Command {

    @Override
    public String send(NodeUser nodeUser, String text) {
        String enableDemoTrading = "";
        int depthGlass;
        String nameChange;
        BigDecimal amountOrder;
        String pair;
        String accountName;
        InetAddress inetAddress;
        String ipAddress;
        StringBuilder settings;

        try {
            if (nodeUser == null) {
                return "Пользователь не найден, введите команду /start";
            }
            if (nodeUser.getMenuChange() == null) {
                return "Биржа не найдена, введите команду /start";
            }
            nameChange = nodeUser.getMenuChange().name();
            amountOrder = nodeUser.getConfigTrade().getAmountOrder();
            pair = nodeUser.getConfigTrade().getNamePair();
            accountName = Optional.of(nodeUser)
                    .map(NodeUser::getAccount)
                    .map(Account::getNameAccount)
                    .orElse("Аккаунт не выбран");

            depthGlass = nodeUser.getConfigTrade().getDepthGlass();

            inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();

            if (nodeUser.getConfigTrade().isEnableDemoTrading()){
                enableDemoTrading = "Внимание! В настоящий момент активирован виртуальный режим трейдинга. " +
                        "Все операции будут производиться без использования реального счета.";
            }
            if (!nodeUser.getConfigTrade().isEnableDemoTrading()){
                enableDemoTrading = "Внимание! В настоящее время активирован реальный режим трейдинга. " +
                        "Все операции будут производиться с использованием реального счета.";
            }
            settings = new StringBuilder()
                    .append("<--[Текущие настройки]--> \n")
                    .append("Текущий аккаунт: ").append(accountName).append(".\n")
                    .append("Биржа: ").append(nameChange).append(".\n")
                    .append("Сумма ордера: $").append(amountOrder).append(".\n")
                    .append("Валютная пара: ").append(pair).append(".\n")
                    .append("Глубина ордеров в стакане: ").append(depthGlass).append(".\n")
                    .append("\n------------------------------------\n")
                    .append(enableDemoTrading).append(".\n")
                    .append("\nip адресс сервера: ").append(ipAddress);
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
