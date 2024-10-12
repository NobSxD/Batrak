package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.castom.Displayable;
import org.example.entity.collect.ChangeType;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Account implements Displayable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;

	private String nameAccount;
	private String publicApiKey;
	private String secretApiKey;
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	@JsonProperty("changeType")
	private ChangeType changeType;

	@ManyToOne
	@JsonBackReference
	private NodeUser nodeUser;

	@Override
	public String getDisplayName() {
		return nameAccount;
	}
}
