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

	private int depthGlass = 100;
	private boolean enableDemoTrading = true;

	@Enumerated(EnumType.STRING)
	@JsonProperty("strategy")
	private MenuStrategy strategy = MenuStrategy.SlidingProtectiveOrder;

	@OneToOne
	@JsonBackReference
	private NodeUser nodeUser;

	@Override
	public String getDisplayName() {
		return namePair;
	}
}
