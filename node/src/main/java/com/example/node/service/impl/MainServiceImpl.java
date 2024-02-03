package com.example.node.service.impl;

import ches.filter.Change;
import com.example.node.dao.ApiKeysDAO;
import com.example.node.dao.RawDataDAO;
import com.example.node.dao.UserMainDAO;
import com.example.node.entity.ApiKeys;
import com.example.node.entity.RawData;
import com.example.node.entity.UserMain;
import com.example.node.entity.enums.ServiceCommand;
import com.example.node.service.AddUserService;
import com.example.node.service.MainService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import trede.TradeBinanceMainImpl;

import javax.transaction.Transactional;

import static com.example.node.entity.enums.UserState.*;


@Service
@Log4j2
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AddUserService addUserService;
    private final UserMainDAO userMainDAO;
    private Change change;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeysDAO apiKeysDAO;

    private ApiKeys apiKeys;

    @Override
    public void processTextButton(Update update) {
        saveRawData(update);
        var userMain = findOrSaveAppUser(update);
        var userState = userMain.getState();
        var output = "";

        var text = update.getCallbackQuery().getData();
        var chatId = update.getCallbackQuery().getMessage().getChatId();


        if (CHANGE.equals(userState)) {
            addChange(userMain, text);
            output = "Вы выбрали " + text + " теперь введите публичный ключ";

        } else if (ACCOUNT_USER.equals(userState)) {
            output = "Вы выбрали " + text + " теперь введите публичный ключ";
        }
        sendAnswer(output, chatId);
    }


    @Transactional
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var userMain = findOrSaveAppUser(update);
        var userState = userMain.getState();
        var output = "";

        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();



        switch (userState) {
            case BASIC_STATE -> output = processServiceCommand(userMain, text);
            case PUBLIC_API -> {
                addPublicApi(userMain, text);
                output = "Вы ввели публичный ключ,  теперь введите секретный ключ";
            }
            case SECRET_API -> {
                addSecretApi(userMain, text);
                output = "Биржа для торгов добавленна";
            }
            case ACCOUNT_USER -> {
                output = processAccountCommand(userMain,text);
            }
            case INFO_ACCOUNT -> {
                output = processAccountCommand(userMain, text);
            }
            case PAIR -> {
                output = processAccountCommand(userMain, text);
            }


            default -> {
                log.error("Unknown user state: " + userState);
                output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
            }
        }




        if (output.equals("Выбирите биржу для регестрации")) {
            sendButton(output, chatId);
        } else if (output.equals("/start_tread")) {
            sendButton("Выбирите биржу для долнейшего дейсвтия", chatId);
        } else {
            sendAnswer(output, chatId);
        }
    }

    private String processAccountCommand(UserMain userMain, String cmd) {
        var accountCommand = ServiceCommand.fromValue(cmd);

        switch (accountCommand){
            case CANCEL -> {
                return cancelProcess(userMain);
            }
            case INFO_ACCOUNT -> {
                userMain.setState(INFO_ACCOUNT);
                userMainDAO.save(userMain);
                return "Выбирите биржу";

            }
            case PAIR -> {
                userMain.setState(PAIR);
                userMainDAO.save(userMain);
                return "выбирите пару";
            }

        }

        return null;
    }

    private String processServiceCommand(UserMain userMain, String cmd) {  //ТОЛЬКО БАЗОВОЕ СОСТОЯНИЕ
        var serviceCommand = ServiceCommand.fromValue(cmd);

        switch (serviceCommand){
            case CANCEL -> {
                return cancelProcess(userMain);
            }
            case REGISTRATION -> {
                return addUserService.registerUser(userMain);
            }
            case HELP -> {
                return help();
            }
            case ACCOUNT -> {
                userMain.setState(ACCOUNT_USER);
                userMainDAO.save(userMain);
                return helpAccount();
            }
            case REGISTER_CHANGE -> {
                addUserApiKeys(userMain);
                userMain.setState(CHANGE);
                userMainDAO.save(userMain);
                return "Выбирите биржу для регестрации";
            }
            default -> {
                return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
            }
        }

    }

    public String treadStart(UserMain userMain, String nameChange) {
        change = new TradeBinanceMainImpl();

        ApiKeys key = apiKeysDAO.findByUserMainAndNameChange(userMain, nameChange);
        String publicKey = key.getPublicApiKey();
        String secretKey = key.getSecretApiKey();
        return change.infoAccount(publicKey, secretKey, userMain.getUsername());
    }

    @Override
    public void treadStop() {

    }


    private void addUserApiKeys(UserMain userMain) {
        apiKeys = new ApiKeys();
        apiKeys.setUserMain(userMain);
    }

    private void addChange(UserMain userMain, String change) {
        apiKeys.setNameChange(change);
        userMain.setState(PUBLIC_API);
        userMainDAO.save(userMain);
    }

    public void addPublicApi(UserMain userMain, String message) {
        apiKeys.setPublicApiKey(passwordEncoder.encode(message));
        userMain.setState(SECRET_API);
        userMainDAO.save(userMain);
    }

    public void addSecretApi(UserMain userMain, String message) {
        apiKeys.setSecretApiKey(passwordEncoder.encode(message));
        userMain.setState(BASIC_STATE);
        apiKeysDAO.save(apiKeys);
        userMainDAO.save(userMain);
    }


    private String cancelProcess(UserMain userMain) {
        userMain.setState(BASIC_STATE);
        userMainDAO.save(userMain);
        return "Команда отменена!";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }


    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private void sendButton(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswerButton(sendMessage);
    }

    private boolean isNotAllowToSendContent(Long chatId, UserMain appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте "
                    + "свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private String help() {
        return """
                Список доступных команд:
                /cancel - отмена выполнения текущей команды;
                /registration - регистрация пользователя;
                /registrationChange - регистрация биржи;
                /account - выбирете биржу;
                """;

    }

    private String helpAccount() {
        return """
                Выбирите действие:
                /infoBalance - информация о балансе;
                /pair - выбор пары;
                /cancel - отмена выполнения текущей команды;
                """;

    }

    private UserMain findOrSaveAppUser(Update update) {

        var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
        var appUserOpt = userMainDAO.findByTelegramUserId(telegramUser.getId());
        if (appUserOpt.isEmpty()) {
            UserMain transientAppUser = UserMain.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return userMainDAO.save(transientAppUser);
        }
        return appUserOpt.get();
    }
}
