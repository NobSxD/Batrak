package org.example.service.impl;

import org.example.service.Rebut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class RebutImpl implements Rebut {
    Logger log = LoggerFactory.getLogger(RebutImpl.class);
    public void restartApplication() {
        try {
            log.warn("Сервис был перезапущен пользователем");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
