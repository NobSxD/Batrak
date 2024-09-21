package org.example.command.menuBasic.tradeSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingsDemoTrading implements Command {
    private final ProducerTelegramService producerTelegramService;

    @Override
    public String send(NodeUser nodeUser, String text) {
        String message = "Хотите включить демо-режим? Введите 'да' для включения, 'нет' для торговли на реальные деньги.";
        producerTelegramService.producerAnswer(message, nodeUser.getChatId());
        nodeUser.setState(UserState.SETTINGS_SAVE_ENABLE_DEMO_TRADING);

        return "";
    }

    @Override
    public UserState getType() {
        return UserState.SETTINGS_ENABLE_DEMO_TRADING;
    }
    @Component
    class SaveDemoTrading implements Command{

        @Override
        public String send(NodeUser nodeUser, String text) {
            if (text.equalsIgnoreCase("да")){
                nodeUser.getConfigTrade().setEnableDemoTrading(true);
                nodeUser.setState(UserState.BASIC_STATE);
                producerTelegramService.mainMenu("Торговля на демо счет", nodeUser.getChatId());
                return "";
            }
            if (text.equalsIgnoreCase("нет")){
                nodeUser.getConfigTrade().setEnableDemoTrading(false);
                nodeUser.setState(UserState.BASIC_STATE);
                producerTelegramService.mainMenu("Торговля на реальный счет", nodeUser.getChatId());
                return "";
            }
            return "Пожалуйста набирите, 'да' или 'нет'";
        }

        @Override
        public UserState getType() {
            return UserState.SETTINGS_SAVE_ENABLE_DEMO_TRADING;
        }
    }
}
