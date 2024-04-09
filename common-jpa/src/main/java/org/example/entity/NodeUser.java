package org.example.entity;

import lombok.*;
import org.example.entity.account.Account;
import org.example.entity.account.AccountBinance;
import org.example.entity.account.AccountMexc;
import org.example.entity.enams.UserState;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "node_user")
@Entity
public class NodeUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long telegramUserId;
	private Long chatId;

	@CreationTimestamp
	private LocalDateTime firstLoginDate;

	private String firstName;

	private String lastName;

	private String username;

	private String email;

	private Boolean isActive;


	@Transient
	private Account account;


	@Enumerated(EnumType.STRING)
	private UserState state;

	@OneToMany(mappedBy = "nodeUser")
	private List<AccountBinance> accountBinances;

	@OneToMany(mappedBy = "nodeUser")
	private List<AccountMexc> accountMexcs;
}
