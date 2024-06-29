package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.entity.enams.OrderType;
import org.example.entity.enams.StrategyEnams;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
public class NodeOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private BigDecimal price;
	private BigDecimal summaCurrency;
	private BigDecimal summaUSD;
	private OrderType orderType;
	private String pair;
	private Date createDate;
	@Enumerated(EnumType.STRING)
	private StrategyEnams strategyEnams;

	@ManyToOne(fetch = FetchType.LAZY)
	private NodeUser nodeUser;

}
