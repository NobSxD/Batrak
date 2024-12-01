package org.example.service.impl;

import org.example.service.Rebut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RebutImpl implements Rebut {
    public void restartApplication(NodeUser nodeUser) {
        try {
            log.warn("Сервис был перезапущен пользователем: {}", nodeUser.getUsername());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
