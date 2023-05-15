package ru.mycom.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mycom.controller.UpdateController;
import ru.mycom.service.AnswerConsumer;

import static ru.mycom.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerIpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerIpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
