package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.command.CommandService;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.MainService;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerTelegramService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static org.example.service.impl.ProcessServiceCommandImpl.extractChatIdFromUpdate;


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
        try {
            if (update == null) {
                throw new IllegalArgumentException("Update cannot be null");
            }
            NodeUser nodeUser = processServiceCommand.findOrSaveAppUser(update);
            String text = extractTextFromUpdate(update);
            if (nodeUser == null) {
                throw new IllegalStateException("NodeUser cannot be null");
            }
            if (isUserInactive(nodeUser)) {
                if (nodeUser.getChatId() == null){
                    long id = extractChatIdFromUpdate(update);
                    nodeUser.setChatId(id);
                }
                denyAccess(nodeUser.getChatId());
                return;
            }


            updateUserState(nodeUser, text);
            String response = commandService.send(nodeUser, text);

            if (response != null && !response.isEmpty()) {
                producerTelegramService.producerAnswer(response, nodeUser.getChatId());
            }
        } catch (Exception e) {
            log.error("Error processing update {}", e.getMessage());
        }
    }

    private boolean isUserInactive(NodeUser user) {
        return user.getIsActive();
    }

    private void denyAccess(long chatId) {
        producerTelegramService.producerAnswer("В доступе отказанно, активируйте свой аккаунт", chatId);
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


}
