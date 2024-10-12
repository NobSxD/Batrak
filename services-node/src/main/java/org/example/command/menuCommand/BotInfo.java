package org.example.command.menuCommand;

import org.example.button.MessageInfo;
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
        try {
            if (nodeUser == null) {
                return MessageInfo.USER_NOT_FOUND;
            }
            if (nodeUser.getChangeType() == null) {
                return MessageInfo.EXCHANGE_NOT_FOUND;
            }
            if (nodeUser.getConfigTrade() == null){
                return MessageInfo.SETTINGS_ACCOUNT_NOT_FOUND;
            }
            String nameChange = nodeUser.getChangeType().name();
            BigDecimal amountOrder = nodeUser.getConfigTrade().getAmountOrder();
            BigDecimal deposit = nodeUser.getConfigTrade().getDeposit();
            String pair = nodeUser.getConfigTrade().getNamePair();
            String accountName = Optional.of(nodeUser)
                    .map(NodeUser::getAccount)
                    .map(Account::getNameAccount)
                    .orElse(MessageInfo.ACCOUNT_NOT_SELECTED);

            double stepBay = nodeUser.getConfigTrade().getStepBay();
            double stepSell = nodeUser.getConfigTrade().getStepSell();

            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress();

            String enableDemoTrading = "";
            if (nodeUser.getConfigTrade().isEnableDemoTrading()){
                enableDemoTrading = MessageInfo.DEMO_MODE_ACTIVE;
            }
            if (!nodeUser.getConfigTrade().isEnableDemoTrading()){
                enableDemoTrading = MessageInfo.REAL_MODE_ACTIVE;
            }
            StringBuilder settings = new StringBuilder()
                    .append(MessageInfo.CURRENT_SETTINGS_HEADER)
                    .append(MessageInfo.CURRENT_ACCOUNT).append(accountName).append(".\n")
                    .append(MessageInfo.EXCHANGE).append(nameChange).append(".\n")
                    .append(MessageInfo.ORDER_AMOUNT).append(amountOrder).append(".\n")
                    .append(MessageInfo.CURRENCY_PAIR).append(pair).append(".\n")
                    .append(MessageInfo.STEP_BAY).append(stepBay).append("%.\n")
                    .append(MessageInfo.STEP_SELL).append(stepSell).append("%.\n")
                    .append(MessageInfo.DEPOSIT).append(deposit).append("$.\n")
                    .append(MessageInfo.SETTINGS_SEPARATOR)
                    .append(enableDemoTrading).append(".\n")
                    .append(MessageInfo.SERVER_IP_ADDRESS).append(ipAddress);
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
