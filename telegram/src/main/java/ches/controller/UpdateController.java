package ches.controller;

import ches.service.UpdateProducer;
import ches.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null");
            return;
        }
        if (update.getMessage() != null){
            distributeMessagesByType(update);
        }if (update.getCallbackQuery() != null){
            distributeMessagesByTypeButton(update);
        }
        else {
            log.error("Received unsupported message type" + update);
        }
    }
    private void distributeMessagesByTypeButton(Update update){
        var message  = update.getCallbackQuery().getData();
        if (message != null){
            processTextMessageButton(update);
        }else {
            setUnsupportedMessageTypeView(update);
        }

    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage().getText();
        if (message != null){
            processTextMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }


   private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }


    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
    private void processTextMessageButton(Update update){
        updateProducer.button(TEXT_BUTTON, update);
    }
}
