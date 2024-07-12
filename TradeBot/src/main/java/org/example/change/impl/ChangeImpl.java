package org.example.change.impl;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.change.Change;
import org.example.change.ChangeFactory;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.TradeState;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.StrategyCommand;
import org.example.strategy.impl.TradeService;
import org.example.xchange.BasicChangeInterface;
import org.knowm.xchange.exceptions.ExchangeException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ChangeImpl implements Change {
	private final Logger logger = Logger.getLogger(ChangeImpl.class);
	private final StrategyCommand strategyCommand;
	private final NodeUserDAO nodeUserDAO;
	private final ProcessServiceCommand processServiceCommand;
	private final TradeService tradeService;


	@Override
	public void instance(NodeUser nodeUser){
		AtomicReference<NodeUser> nodeUserRef = new AtomicReference<>(nodeUser);
		nodeUser.setStateTrade(TradeState.BAY);
		tradeStart(nodeUserRef, nodeUser);
	}


	public void tradeStart(AtomicReference<NodeUser> nodeUserRef, NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);

			Observable.just(nodeUserRef.get())
					.repeatWhen(completed -> completed.delay(100, TimeUnit.MILLISECONDS)) // Задержка перед повторением
					.takeUntil(currentNodeUser -> !currentNodeUser.isTradeStartOrStop()) // Закончить, когда isTradeStartOrStop() вернет false
					.flatMap(currentNodeUser -> {
						currentNodeUser.setStateTrade(TradeState.BAY);

						return tradeService.executeTrade(currentNodeUser, change)
								.flatMap(result -> {
									System.out.println("Результат торговли: " + result);

									// Обновление объекта nodeUser
									NodeUser updatedNodeUser = nodeUserDAO.findByTelegramUserId(currentNodeUser.getId()).orElse(currentNodeUser);
									nodeUserRef.set(updatedNodeUser);

									return Observable.just(updatedNodeUser);
								});
					})
					.subscribeOn(Schedulers.io())
					.subscribe(resultNodeUser -> {
						// Обновление nodeUser после выполнения торговли
						// Сюда дописывать ничего не надо, так как nodeUserRef уже обновлен.
					}, error -> {
						// Обработка ошибки
						logger.error("Ошибка выполнения торговли: " + error.getMessage(), error);
						nodeUserRef.get().setTradeStartOrStop(false);
					});

		} catch (ExchangeException e) {
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			logger.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		} catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			logger.error(e.getMessage() + " имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoAccount(NodeUser nodeUser) {
		try {
			BasicChangeInterface change = ChangeFactory.createChange(nodeUser);
			processServiceCommand.sendAnswer( change.accountInfo(), nodeUser.getChatId());
		}catch (ExchangeException e){
			processServiceCommand.sendAnswer("Проверте правелность введных данных, api public key, api secret key", nodeUser.getChatId());
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
		}
		catch (Exception e) {
			nodeUser.setTradeStartOrStop(false);
			logger.error(e.getMessage() + "имя пользователя: " +  nodeUser.getUsername() + " id пользователя " + nodeUser.getId());
		}
	}

	@Override
	public void infoBalanceAccount(NodeUser nodeUser) {

	}

	@Override
	public void cancelAllOrder(NodeUser nodeUser) {

	}


}
