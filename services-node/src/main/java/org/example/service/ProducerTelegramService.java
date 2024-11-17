package org.example.service;

import org.example.dto.NodeUserDto;

import java.util.List;

import org.example.entity.Account;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;

public interface ProducerTelegramService {
    void producerAnswer(String output, Long chatId);
    void menuChange(List<NodeChange> nameChange, String output, Long chatId);
    void menuMain(String output, Long chatId);
    void menuTrade(String output, Long chatId);
    void menuAccounts(List<Account> accounts, String output, Long chatId);
    void menuOperation(String output, Long chatId);
    void menuPair(List<Pair> pairs, String output, Long chatId);
    void menuAdmin(String output, Long chatId);
    void menuBan(List<NodeUserDto> userName, String output, Long chatId);

    void menuStatistics(String output, Long chatId);
}
