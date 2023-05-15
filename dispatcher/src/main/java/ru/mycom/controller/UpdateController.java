package ru.mycom.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mycom.service.UpdateProducer;
import ru.mycom.utils.MessageUtils;

import static ru.mycom.RabbitQueue.*;

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

    public void registerBot(TelegramBot bot){
        this.telegramBot = bot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null");
        }

        if (update.hasMessage()){
            distributeMessageByType(update);
        }
        else {
            log.error("unsupported message type is received:" + update);
        }

    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if(message.hasText())
            processTextMessage(update);
        else if (message.hasDocument())
            processDocMessage(update);
        else if (message.hasPhoto())
            processPhotoMessage(update);
        else
            setUnsupportedMessage(update);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setUnsupportedMessage(Update update) {
        messageUtils.generateMessageWithText(update,  "Неподдерживаемый тип сообщения!");
        setFileIsReceivedView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateMessageWithText(update,  "Файл получен! Обрабатывается...");
        setView(sendMessage);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);

    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
