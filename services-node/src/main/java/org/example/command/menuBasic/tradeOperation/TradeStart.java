package org.example.command.menuBasic.tradeOperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerXChangeService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Класс TradeStart реализует интерфейс Command и отвечает за запуск процесса трейдинга
 * для пользователя. Он использует сервисы для взаимодействия с внешними системами
 * и базой данных.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStart implements Command, RoleProvider {

    private final ProducerXChangeService producerXChangeService;
    private final NodeUserDAO nodeUserDAO;

    /**
     * Этот метод используется для обновления закешированных данных о пользователе
     * для получения актуальной информации о состоянии трейдинга. В зависимости от
     * обновленных данных определяет, нужно ли запускать трейдинг для данного пользователя
     * через ProducerXChangeService.
     *
     * @param nodeUser объект пользователя, для которого выполняется команда
     * @param text     дополнительный текст, сопровождающий команду (не используется)
     * @return Пустую строку, если трейдинг успешно запущен, или сообщение об ошибке,
     * если трейдинг уже был запущен или возникли проблемы.
     */
    @Override
    public String send(NodeUser nodeUser, String text) {
        try {
            nodeUser = nodeUserDAO.findById(nodeUser.getId()).orElse(nodeUser);
            nodeUser.setLastStartTread(LocalDateTime.now());
            producerXChangeService.startTrade(nodeUser);
            return "";

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
            return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
        }
    }



    /**
     * Получает тип текущей команды.
     *
     * @return UserState.TRADE_START, обозначающий тип команды
     */
    @Override
    public UserState getType() {
        return UserState.TRADE_START;
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }


}
