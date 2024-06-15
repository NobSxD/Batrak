package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter

public class Statistics {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private BigDecimal profit = new BigDecimal("00.00");
	private int countDeal = 0;
	private int countDealBay = 0;
	private int countDealSel = 0;

	@OneToOne
	private NodeUser nodeUser;

}
