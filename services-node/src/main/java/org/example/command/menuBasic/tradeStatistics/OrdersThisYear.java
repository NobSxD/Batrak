package org.example.command.menuBasic.tradeStatistics;

import org.example.button.ButtonLabelManager;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.example.dao.NodeOrdersDAO;

import org.springframework.stereotype.Component;

import static org.example.command.menuBasic.tradeStatistics.helper.NodeOrderService.messageStatistics;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersThisYear implements Command, RoleProvider {
    private final NodeOrdersDAO nodeOrdersDAO;
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).with(LocalTime.MIN);
            List<NodeOrder> allOrdersThisYear =
                    nodeOrdersDAO.findAllOrdersThisYear(startOfYear, nodeUser.getId(), nodeUser.getConfigTrade().isEnableDemoTrading());

            return messageStatistics(allOrdersThisYear, ButtonLabelManager.thisYear);
        } catch (Exception e) {
            log.error("В методе OrdersThisYear произошла ошибка {}", e.getMessage());
            producerTelegramService.producerAnswer(e.getMessage(), nodeUser.getChatId());
        }
        return "";
    }

    @Override
    public UserState getType() {
        return UserState.STATISTICS_YEAR;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
