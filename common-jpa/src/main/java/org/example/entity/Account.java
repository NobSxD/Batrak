package org.example.entity;


import lombok.*;
import org.example.entity.enams.ChangeType;

import javax.persistence.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public  class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected long id;

	private String nameAccount;
	private String publicApiKey;
	private String secretApiKey;
	@Enumerated(EnumType.STRING)
	private ChangeType changeType;


	@OneToOne(cascade = CascadeType.MERGE)
	private NodeUser nodeUser;

}
