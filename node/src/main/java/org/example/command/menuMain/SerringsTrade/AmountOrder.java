package org.example.command.menuMain.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.exeptions.NumberTooSmallException;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class AmountOrder implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	private static final Logger logger = Logger.getLogger(AmountOrder.class);
	@Override
	public String send(NodeUser nodeUser, String text) {
		try{
			nodeUser.setState(UserState.SETTINGS_SAVE_AMOUNT_ORDER);
			nodeUserDAO.save(nodeUser);
			return "Укажите сумму ордера";
		}catch (Exception e){
			logger.error(e.getMessage());
			return "При переходе в меню, изменение суммы ордера произошла ошибка, обратитесь к администратору";
		}
	}

	@Override
	public UserState getType() {
		return UserState.SETTINGS_AMOUNT_ORDER;
	}

	@Component
	class SaveAmountOrder implements Command{

		@Override
		public String send(NodeUser nodeUser, String text) {
			try{
				double amount = Double.parseDouble(text);
				if(amount < 11){
					throw new NumberTooSmallException();
				}

				BigDecimal amountBigDecimal = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
				logger.info(" пользовтель: " + nodeUser.getUsername()
									+ " id: " + nodeUser.getId()
									+ " сумма ордера = " + amountBigDecimal);
				nodeUser.getConfigTrade().setAmountOrder(amountBigDecimal);
				settingsTradeDAO.save(nodeUser.getConfigTrade());
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu3TradeSettings("Цена ордера успешно изменена. Новая цена: " + text, nodeUser.getChatId());
				return "";
			}catch (NumberTooSmallException e){
				logger.error(e.getMessage() + " пользовтель: " +  nodeUser.getUsername() + " id: " + nodeUser.getId());
				return "Вы ввели число меньше 11, минимальный размер ордера 11";
			}
			catch (NumberFormatException e){
				logger.error(e.getMessage() + " пользовтель: " +  nodeUser.getUsername() + " id: " + nodeUser.getId());
				return "Ведите пожалуйсто число формат 100.22";
			}
			catch (NullPointerException e){
				logger.error(e.getMessage() + " пользовтель: " +  nodeUser.getUsername() + " id: " + nodeUser.getId());
				return "null";
			}
			catch (Exception e){
				logger.error(e.getMessage() + " пользовтель: " +  nodeUser.getUsername() + " id: " + nodeUser.getId());
				return "Произошла неожиданная ошибка при изменении суммы ордера, пожалуйста обратитесь к администратору";
			}
		}

		@Override
		public UserState getType() {
			return UserState.SETTINGS_SAVE_AMOUNT_ORDER;
		}
	}
}
