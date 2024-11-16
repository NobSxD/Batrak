package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.example.castom.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.example.entity.enams.menu.MenuStrategy;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigTrade  implements Displayable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String namePair = "-";
	int scale;

	private BigDecimal amountOrder = new BigDecimal(11);
	private BigDecimal deposit = new BigDecimal(300);

	private double stepBay = 0.01;
	private double stepSell = 0.005;
	private boolean enableDemoTrading = false;

	@Enumerated(EnumType.STRING)
	@JsonProperty("strategy")
	private MenuStrategy strategy = MenuStrategy.GridTrading;

	@OneToOne(cascade = CascadeType.ALL)
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
	public double getStepBayD() {
		return stepBay;
	}

	public double getStepSell() {
		return stepSell * 100;
	}
	public double getStepSellD() {
		return stepSell;
	}

	public void setStepSell(double stepSell) {
		this.stepSell = stepSell / 100;
	}
}
