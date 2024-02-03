package com.example.node.service;

import com.example.node.entity.UserMain;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    String treadStart(UserMain userMain, String nameChange);
    void treadStop();

    void processTextButton(Update update);
}
