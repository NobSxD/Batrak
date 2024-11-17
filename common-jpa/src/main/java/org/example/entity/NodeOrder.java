package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.example.entity.enams.menu.MenuStrategy;
import org.example.entity.enams.state.OrderState;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("orderId")
	private String orderId;
	
	@JsonProperty("originalAmount")
	@Column(precision = 12, scale = 6)
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
	@JsonProperty("menuStrategy")
	private MenuStrategy menuStrategy;
	
	@ManyToOne()
	@JsonBackReference
	private NodeUser nodeUser;
	
	@JsonProperty("checkReal")
	private boolean checkReal;

	@Enumerated(EnumType.STRING)
	@JsonProperty("orderState")
	private OrderState orderState;
	
	@Builder
	public NodeOrder(Long id, String type, String orderId, BigDecimal originalAmount, BigDecimal limitPrice, BigDecimal cumulativeAmount,
			BigDecimal averagePrice, BigDecimal usd, String instrument, Date timestamp,
			String userReference, MenuStrategy menuStrategy, NodeUser nodeUser, boolean checkReal, OrderState orderState) {
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
		this.menuStrategy = menuStrategy;
		this.nodeUser = nodeUser;
		this.checkReal = checkReal;
		this.orderState = orderState;
	}
	
	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
