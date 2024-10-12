package org.example.service;

import org.example.entity.Account;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;

import java.util.List;

public interface ProducerTelegramService {
    void producerAnswer(String output, Long chatId);
    void changeMenu(List<NodeChange> nameChange, String output, Long chatId);
    void mainMenu(String output, Long chatId);
    void menuTrade(String output, Long chatId);
    void accountsMenu(List<Account> accounts, String output, Long chatId);
    void menuOperation(String output, Long chatId);
    void pairMenu(List<Pair> pairs, String output, Long chatId);


}
