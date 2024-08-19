package org.example.strategy.impl.helper;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CreateNodeOrder {
	
	public static NodeOrder createNodeOrder(LimitOrder limitOrder, String orderId, List<BigDecimal> priceAndAmount, NodeUser nodeUser) {
		BigDecimal usd = CurrencyConverter.convertUsdt(priceAndAmount.get(1), priceAndAmount.get(0));
		return NodeOrder.builder()
						.type(limitOrder.getType().name())
						.orderId(orderId)
						.originalAmount(limitOrder.getOriginalAmount())
						.limitPrice(limitOrder.getLimitPrice())
						.cumulativeAmount(limitOrder.getCumulativeAmount())
						.averagePrice(limitOrder.getAveragePrice())
						.usd(usd)
						.instrument(limitOrder.getInstrument().toString())
						.timestamp(new Date())
						.userReference(limitOrder.getUserReference())
						.checkReal(nodeUser.getConfigTrade().isRealTrade())
						.strategyEnams(nodeUser.getConfigTrade().getStrategy())
						.nodeUser(nodeUser)
						.build();
	}
	
}
