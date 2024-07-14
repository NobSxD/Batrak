package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.example.entity.enams.OrderType;
import org.example.entity.enams.StrategyEnams;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String orderId;
	private BigDecimal originalAmount;
	private BigDecimal price;

	private BigDecimal summaUSD;
	private OrderType orderType;
	private String pair;
	private Date createDate;
	@Enumerated(EnumType.STRING)
	private StrategyEnams strategyEnams;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private NodeUser nodeUser;

}
