package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter

public class SettingsTrade {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String namePair = "BTC-USDT";

	private BigDecimal amountOrder = BigDecimal.valueOf(11);

	private int depthGlass = 25;

	@OneToOne
	private NodeUser nodeUser;
}
