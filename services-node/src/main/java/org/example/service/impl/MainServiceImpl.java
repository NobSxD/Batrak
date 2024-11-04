package org.example.service.impl;

import jakarta.transaction.Transactional;
import org.example.command.CommandService;
import org.example.service.MainService;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerTelegramService;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Service;

import static org.example.button.MessageInfo.USER_NOT_FOUND_MESSAGE;


@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {

    private final ProcessServiceCommand processServiceCommand;
    private final ProducerTelegramService producerTelegramService;
    private final CommandService commandService;
    private final Map<String, UserState> buttonRegistration;

    @Override
    @Transactional
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update object is null");
            return;
        }

        try {
            NodeUser nodeUser = processServiceCommand.findOrSaveAppUser(update);
            if (nodeUser == null) {
                producerTelegramService.producerAnswer(USER_NOT_FOUND_MESSAGE, processServiceCommand.extractChatIdFromUpdate(update));
                log.error("NodeUser cannot be null in processUpdate");
                throw new IllegalStateException("NodeUser cannot be null");
            }

            String text = extractTextFromUpdate(update);
            if (nodeUser.getIsActive()){
                producerTelegramService.producerAnswer("В доступе отказанно, активируйте свой аккаунт", nodeUser.getChatId());
            } else {
                processUserCommand(nodeUser, text);
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", e.getMessage(), e);
        }
    }

    private String extractTextFromUpdate(Update update) {
        if (update.getMessage() != null) {
            return update.getMessage().getText();
        } else if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getData();
        }
        return "";
    }

    private void updateUserState(NodeUser user, String text) {
        UserState userState = buttonRegistration.get(text);
        if (userState != null) {
            user.setState(userState);
        }
    }

    private void processUserCommand(NodeUser nodeUser, String text) {
        if (text == null || text.isEmpty()) {
            log.warn("Text extracted from update is null or empty");
            return;
        }

        updateUserState(nodeUser, text);
        String response = commandService.send(nodeUser, text);
        if (response != null && !response.isEmpty()) {
            producerTelegramService.producerAnswer(response, nodeUser.getChatId());
        }
    }


}
