package org.example.service.impl;

import org.example.castom.CustomMessage;
import org.example.castom.Displayable;
import org.example.castom.MessageWrapperDTO;
import org.example.controller.UpdateController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.example.model.RabbitQueue.*;


@Service
public class AnswerConsumerImpl{
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }


    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(CustomMessage customMessage) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(customMessage.getChatId())
                .text(customMessage.getText())
                .build();
        updateController.setView(sendMessage);
    }


    @RabbitListener(queues = ENUM_CUSTOM_MESSAGE)
    public <T extends Enum<T>> void processEnumMenu(MessageWrapperDTO messageWrapperDTO) {
        Class<?> enumClass = messageWrapperDTO.getEnumClass();
        CustomMessage customMessage = messageWrapperDTO.getCustomMessage();
        T[] enumValues = (T[]) enumClass.getEnumConstants();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (T enumValue : enumValues) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(button(enumValue.toString()));
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(customMessage.getChatId())
                .text(customMessage.getText())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        updateController.setView(sendMessage);
    }

    @RabbitListener(queues = LIST_CUSTOM_MESSAGE)
    public void accountsMenu(MessageWrapperDTO messageWrapperDTO) {
        List<? extends Displayable> accounts = messageWrapperDTO.getObject();
        CustomMessage customMessage = messageWrapperDTO.getCustomMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Displayable addChangeEnums : accounts  ) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(button(addChangeEnums.getDisplayName()));
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(customMessage.getChatId())
                .text(customMessage.getText())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        updateController.setView(sendMessage);
    }

    private InlineKeyboardButton button(String text) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(text)
                .build();
    }
}
