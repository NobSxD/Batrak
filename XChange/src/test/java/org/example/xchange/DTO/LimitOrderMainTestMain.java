package org.example.xchange.DTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class LimitOrderMainTestMain {
	@Test
	public void testUnmarshal() throws IOException {
		InputStream is = LimitOrderMainTestMain.class.getResourceAsStream("/org/example/xchange/dto.trade/example-place-limit-order.json");
		ObjectMapper mapper = new ObjectMapper();
		LimitOrderMain limitOrderMainBinance = mapper.readValue(is, LimitOrderMain.class);
		assertThat(limitOrderMainBinance.getLimitPrice()).isEqualTo(new BigDecimal("50000"));
		assertThat(limitOrderMainBinance.getOrderMain().getType()).isEqualTo(Order.OrderType.BID);
		assertThat(limitOrderMainBinance.getOrderMain().getOriginalAmount()).isEqualTo(new BigDecimal("0.00022"));
		assertThat(limitOrderMainBinance.getOrderMain().getCumulativeAmount()).isNull();
		assertThat(limitOrderMainBinance.getOrderMain().getAveragePrice()).isNull();
		assertThat(limitOrderMainBinance.getOrderMain().getInstrument()).isEqualTo(new CurrencyPair("BTC-USDT"));
		assertThat(limitOrderMainBinance.getOrderMain().getId()).isEqualTo("");
		assertThat(limitOrderMainBinance.getOrderMain().getTimestamp()).isEqualTo(new Date(1381787133l));
		assertThat(limitOrderMainBinance.getOrderMain().getStatus()).isNull();
		assertThat(limitOrderMainBinance.getOrderMain().getUserReference()).isEqualTo(108789836);
	}

}