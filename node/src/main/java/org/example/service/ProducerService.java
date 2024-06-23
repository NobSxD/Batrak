package org.example.service;

import org.example.entity.account.Account;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public interface ProducerService {
    void producerAnswer(SendMessage sendMessage);
    void producerChangeEnumsButton(SendMessage sendMessage);
    void producerMenuEnumsButton(SendMessage sendMessage);
    void producerMenuTradeEnumsButton(SendMessage sendMessage);

    void producerMenuListStrategy(SendMessage sendMessage);

    void producerMenuListAccount(List<Account> accounts, SendMessage sendMessage);

}
