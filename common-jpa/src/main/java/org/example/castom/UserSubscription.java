package org.example.castom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class UserSubscription {
	private String userId;
	private String currencyPair;
	private BigDecimal targetRate;
}
