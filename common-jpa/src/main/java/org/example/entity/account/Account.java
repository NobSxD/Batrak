package org.example.entity.account;


import lombok.*;
import org.example.entity.NodeUser;
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
	private ChangeType changeType;


	@OneToOne
	private NodeUser nodeUser;

	@ManyToOne(fetch = FetchType.EAGER)
	private NodeUser nodeUsers;
}
