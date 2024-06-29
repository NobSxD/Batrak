package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.enams.StrategyEnams;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter

public class ConfigTrade {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String namePair = "BTC-USDT";

	private BigDecimal amountOrder = new BigDecimal(11);

	private int depthGlass = 100;

	@Enumerated(EnumType.STRING)
	private StrategyEnams strategy = StrategyEnams.SlidingProtectiveOrder;

	@OneToOne
	private NodeUser nodeUser;
}
