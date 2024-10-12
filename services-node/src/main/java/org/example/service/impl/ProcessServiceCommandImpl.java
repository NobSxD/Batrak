package org.example.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import org.example.dao.NodeUserDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.entity.enams.state.UserState.BASIC_STATE;

@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {

    private final NodeUserDAO nodeUserDAO;
    private final Cache<Long, NodeUser> nodeUserCache;


    @Override
    public NodeUser findOrSaveAppUser(Update update) {

        var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
        NodeUser nodeUser = nodeUserCache.getIfPresent(telegramUser.getId());
        if (nodeUser == null) {
            var appUserOpt = nodeUserDAO.findByTelegramUserId(telegramUser.getId());
            if (appUserOpt.isEmpty()) {
                ConfigTrade settingsTrade = new ConfigTrade();
                nodeUser = NodeUser.builder()
                        .chatId(extractChatIdFromUpdate(update))
                        .telegramUserId(telegramUser.getId())
                        .username(telegramUser.getUserName())
                        .firstName(telegramUser.getFirstName())
                        .lastName(telegramUser.getLastName())
                        .configTrade(settingsTrade)
                        .isActive(false)
                        .state(BASIC_STATE)
                        .build();
                settingsTrade.setNodeUser(nodeUser);
                nodeUserDAO.save(nodeUser);
            } else {
                nodeUser = appUserOpt.get();
                nodeUserCache.put(telegramUser.getId(), nodeUser);
            }
        }
        return nodeUser;
    }

    public static long extractChatIdFromUpdate(Update update) {
        if (update.getMessage() != null) {
            return update.getMessage().getChatId();
        } else if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return 0;
    }

}
