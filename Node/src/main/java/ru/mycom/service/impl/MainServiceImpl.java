package ru.mycom.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mycom.dao.AppUserDAO;
import ru.mycom.dao.RawDataDAO;
import ru.mycom.entity.AppDocument;
import ru.mycom.entity.AppUser;
import ru.mycom.entity.RawData;
import ru.mycom.exception.UploadFileException;
import ru.mycom.service.FileService;
import ru.mycom.service.MainService;
import ru.mycom.service.ProducerService;
import ru.mycom.service.enums.ServiceCommands;

import static ru.mycom.entity.enums.UserState.BASIC_STATE;
import static ru.mycom.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.mycom.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl  implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;


    public MainServiceImpl(RawDataDAO rawDataDAO,
                           ProducerService producerService,
                           AppUserDAO appUserDAO,
                           FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // TODO добавить обработку емейла
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }


        var chatId = update.getMessage().getChatId();
        sendMessage(output, chatId);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return ;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            //TODO Добавить генерацию ссылки для скачивания документа
            var answer = "Документ успешно загружен! Ссылка для скачивания http://test.ru/get-doc/777";
            sendMessage(answer, chatId);
        }
        catch (UploadFileException e){
            log.error(e);
            String error = "К сожалению, загрузка файда не удалась. Повторите попытку позже.";
            sendMessage(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            var error = "Зарегестрируйтесь или активируйте свою учётную запись для загрузки контента.";
            sendMessage(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendMessage(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return ;
        }

        //TODO добавить сохранение фото :)
        var answer = "Фото успешно загружно! Ссылка для скачивания http://test.ru/get-photo/777";
        sendMessage(answer, chatId);
    }

    private void sendMessage(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.ProduceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            // TODO добавить регистрацию
            return "временно недоступно";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую, чтобы просмостреть доступные команды отправте /help";
        } else {
            return "Неизвестная команда, чтобы просмостреть доступные команды отправте /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды; \n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firtName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавление регистрации
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
