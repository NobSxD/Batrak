package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.example.entity.enams.StrategyEnams;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class NodeOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@JsonProperty("type")
	private String type;
	
	private String orderId;
	
	@JsonProperty("originalAmount")
	@Column(precision = 10, scale = 10)
	private BigDecimal originalAmount;
	
	@JsonProperty("limitPrice")
	private BigDecimal limitPrice;
	
	@JsonProperty("cumulativeAmount")
	private BigDecimal cumulativeAmount;
	
	@JsonProperty("averagePrice")
	private BigDecimal averagePrice;
	
	@JsonProperty("usd")
	private BigDecimal usd;
	
	@JsonProperty("instrument")
	private String instrument;
	
	@JsonProperty("timestamp")
	private LocalDateTime timestamp;
	
	@JsonProperty("userReference")
	private String userReference;
	
	@Enumerated(EnumType.STRING)
	private StrategyEnams strategyEnams;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private NodeUser nodeUser;
	
	@JsonProperty("checkReal")
	private boolean checkReal;
	
	@Builder
	public NodeOrder(Long id, String type, String orderId, BigDecimal originalAmount, BigDecimal limitPrice, BigDecimal cumulativeAmount,
			BigDecimal averagePrice, BigDecimal usd, String instrument, Date timestamp,
			String userReference, StrategyEnams strategyEnams, NodeUser nodeUser, boolean checkReal) {
		this.id = id;
		this.type = type;
		this.orderId = orderId;
		this.originalAmount = originalAmount;
		this.limitPrice = limitPrice;
		this.cumulativeAmount = cumulativeAmount;
		this.averagePrice = averagePrice;
		this.usd = usd;
		this.instrument = instrument;
		this.timestamp = timestamp != null ? convertToLocalDateTime(timestamp) : null;
		this.userReference = userReference;
		this.strategyEnams = strategyEnams;
		this.nodeUser = nodeUser;
		this.checkReal = checkReal;
	}
	
	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
