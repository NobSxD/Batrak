package org.example.service.impl;

import org.example.configuration.CurrencyProperties;
import org.example.dao.NodeChangeDAO;
import org.example.dao.PairDAO;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;
import org.example.entity.enams.menu.MenuChange;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyProperties currencyProperties;
    private final NodeChangeDAO nodeChangeDAO;
    private final PairDAO pairDAO;

    @PostConstruct
    @Transactional
    public void saveNodeChanges() {
        currencyProperties.getExchanges().forEach((name, exchange) -> {
            MenuChange menuChange = MenuChange.fromValue(exchange.getType());
            NodeChange nodeChange = nodeChangeDAO.findByMenuChange(menuChange)
                    .orElseGet(() -> NodeChange.builder()
                            .menuChange(menuChange)
                            .build());

            // Удаление старых пар
            nodeChange.setPairs(new HashMap<>());
            nodeChangeDAO.save(nodeChange); // Очищаем старые пары
            // Добавление новых пар
            exchange.getPairs().forEach(pairConfig -> {
                String currencyPair = pairConfig.getNamePair();
                int scale = pairConfig.getScale();
                Pair pair = new Pair(currencyPair, scale);
                pairDAO.save(pair);
                nodeChange.addPair(pair);
            });
            nodeChangeDAO.save(nodeChange);
        });
    }
}
