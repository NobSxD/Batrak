package org.example.entity;

import lombok.*;
import org.example.change.Change;
import org.example.entity.account.Account;
import org.example.entity.enams.Menu1Enums;
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


	@OneToOne
	private Account account;



	@Transient
	private Change change;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List <Account> accounts;


	@Enumerated(EnumType.STRING)
	private Menu1Enums menu1Enums;

	@Enumerated(EnumType.STRING)
	private UserState state;

	@Enumerated(EnumType.STRING)
	private UserState menuState;


}
