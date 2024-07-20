package org.example.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.example.entity.enams.ChangeType;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public  class Account{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;

	private String nameAccount;
	private String publicApiKey;
	private String secretApiKey;
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	private ChangeType changeType;


	@ManyToOne(cascade = CascadeType.MERGE)
	@JsonBackReference
	private NodeChange nodeChange;

	@ManyToOne
	@JsonBackReference("nodeUser-account")
	private NodeUser nodeUser;

}
