package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.example.castom.Displayable;
import org.example.entity.collect.ChangeType;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
