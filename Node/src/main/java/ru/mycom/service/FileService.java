package ru.mycom.service;


import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mycom.entity.AppDocument;
import ru.mycom.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
