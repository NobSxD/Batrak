package org.example.entity;

import org.example.castom.Displayable;
import org.example.entity.enams.menu.MenuStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigTrade  implements Displayable {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String namePair = "BTC-USDT";
	int scale = 5;

	private BigDecimal amountOrder = new BigDecimal(11);
	private BigDecimal deposit = new BigDecimal(300);

	private double stepBay = 0.01;
	private double stepSell = 0.005;
	private boolean enableDemoTrading = false;

	@Enumerated(EnumType.STRING)
	@JsonProperty("strategy")
	private MenuStrategy strategy = MenuStrategy.GridTrading;

	@OneToOne
	@JsonBackReference
	private NodeUser nodeUser;

	@Override
	public String getDisplayName() {
		return namePair;
	}

	public void setStepBay(double stepBay) {
		this.stepBay = stepBay / 100;
	}
	public double getStepBay() {
		return stepBay * 100;
	}

	public double getStepSell() {
		return stepSell * 100;
	}

	public void setStepSell(double stepSell) {
		this.stepSell = stepSell / 100;
	}
}
