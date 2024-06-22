package org.example.entity;

import lombok.*;
import org.example.entity.account.Account;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.TradeState;
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
	private String nameStrategy;


	@OneToOne
	private Account account;


	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "nodeUser")
	private List <NodeOrder> orders;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "nodeUsers")
	private List <Account> accounts;


	@Enumerated(EnumType.STRING)
	private ChangeType changeType;

	@Enumerated(EnumType.STRING)
	private UserState state;

	@Enumerated(EnumType.STRING)
	private TradeState stateTrade;

	@OneToOne
	private SettingsTrade settingsTrade;

	@OneToOne
	private Statistics statistics;

	private boolean tradeStartOrStop;


}
