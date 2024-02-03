package com.example.node.service.impl;

import com.example.node.entity.enums.Change;
import com.example.node.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE;


@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void producerAnswerButton(SendMessage sendMessages) {
        Change[] changes = Change.values(); //список бирж для добавление

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Change addChange : changes) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(button(addChange.toString()));
            rowList.add(keyboardButtonsRow);
        }


        inlineKeyboardMarkup.setKeyboard(rowList);

        sendMessages.setReplyMarkup(inlineKeyboardMarkup);
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessages);

    }

    private InlineKeyboardButton button(String text) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(text)
                .build();
    }

}

