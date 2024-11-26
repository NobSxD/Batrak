package org.example.service.impl;

import org.example.service.ProcessServiceCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.Data;

import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;

import org.example.dao.NodeUserDAO;

import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.BASIC_STATE;

@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {

    private final NodeUserDAO nodeUserDAO;


    @Override
    public NodeUser findOrSaveAppUser(Update update) {

        var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();

        var appUserOpt = nodeUserDAO.findByTelegramUserId(telegramUser.getId()).orElse(null);
        if (appUserOpt == null) {
            ConfigTrade settingsTrade = new ConfigTrade();
            appUserOpt = NodeUser.builder()
                    .chatId(extractChatIdFromUpdate(update))
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .configTrade(settingsTrade)
                    .isActive(false)
                    .state(BASIC_STATE)
                    .role(Role.USER)
                    .build();
            settingsTrade.setNodeUser(appUserOpt);
            nodeUserDAO.save(appUserOpt);
        }

        return appUserOpt;
    }

    @Override
    public long extractChatIdFromUpdate(Update update) {
        if (update.getMessage() != null) {
            return update.getMessage().getChatId();
        } else if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return 0;
    }

}
