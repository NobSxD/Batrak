package org.example.entity.account;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.entity.NodeUser;

import javax.persistence.*;


@Entity
@Inheritance(
		strategy = InheritanceType.TABLE_PER_CLASS
)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder

public abstract class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;

	protected String nameChange;
	protected String publicApiKey;
	protected String secretApiKey;


	@OneToOne(fetch = FetchType.EAGER)
	private NodeUser nodeUser;
}
