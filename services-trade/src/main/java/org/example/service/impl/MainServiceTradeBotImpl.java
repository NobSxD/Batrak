package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.change.ChangeFactory;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.OrderState;
import org.example.entity.enams.state.TradeState;
import org.example.service.MainServiceTradeBot;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyCommand;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
/*
    PLACED,        // Ордер был выставлен на продажу или покупку, но еще не исполнен.
    PARTIALLY_FILLED, // Ордер частично исполнен. Некоторая часть заказа выполнена.
    FILLED,        // Ордер полностью исполнен.
    CANCELLED,     // Ордер был отменен пользователем или системой.
    PENDING_CANCEL, // Запрос на отмену ордера инициирован, но еще не подтвержден.
    EXPIRED,       // Срок действия ордера истек до его исполнения.
    REJECTED       // Ордер был отклонен системой из-за ошибок или неправильных данных.

 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceTradeBotImpl implements MainServiceTradeBot {
	private final StrategyCommand strategyCommand;
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public void startORStopTrade(@Payload NodeUser nodeUser) {
		try {
			nodeUser.setStateTrade(TradeState.BAY);
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			processServiceCommand.sendAnswer("Трейдинг запущен", nodeUser.getChatId());
			strategyCommand.trade(nodeUser, change);
			
		} catch (ExchangeException e) {
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key, или добавте разрешенный ip server", nodeUser.getChatId());
			log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
		} catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			log.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
			log.error("Имя пользователя: {}. id: {}. Ошибка: {}.", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
		}
	}
	
	public void infoAccount(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			processServiceCommand.sendAnswer( change.accountInfo(), nodeUser.getChatId());
		}catch (ExchangeException e){
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			log.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		}
		catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			log.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}
	public void cancelOrder(NodeUser nodeUser){
		List<NodeOrder> orders = nodeUser.getOrders();
		if (!orders.isEmpty()){
			NodeOrder nodeOrder = orders.get(orders.size() - 1);
			nodeOrder.setOrderState(OrderState.PENDING_CANCEL);
		}
	}
}
