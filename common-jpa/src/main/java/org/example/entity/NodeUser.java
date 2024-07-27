package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime firstLoginDate;

	private String firstName;

	private String lastName;

	private String username;

	private String email;

	private Boolean isActive;



	@OneToOne
	protected Account account;


	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "nodeUser")
	private List <NodeOrder> orders;

	@JsonManagedReference("nodeUser-account")
	@OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true, mappedBy = "nodeUser")
	private List <Account> accounts;

	@OneToOne
	@JsonManagedReference
	private ConfigTrade configTrade;

	@OneToOne
	@JsonManagedReference
	private Statistics statistics;


	@Enumerated(EnumType.STRING)
	private ChangeType changeType;

	@Enumerated(EnumType.STRING)
	private UserState state;

	@Enumerated(EnumType.STRING)
	private TradeState stateTrade;


	private boolean tradeStartOrStop;

}
