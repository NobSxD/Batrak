package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.TradeState;
import org.example.entity.enams.UserState;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Table(name = "node_user")
@Entity
@NoArgsConstructor
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
	
	@CreationTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime lastStartTread;
	
	@OneToOne
	protected Account account;
	
	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "nodeUser")
	private List<NodeOrder> orders;
	
	@JsonManagedReference("nodeUser-account")
	@OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true, mappedBy = "nodeUser")
	private List<Account> accounts;
	
	@OneToOne
	@JsonManagedReference
	private ConfigTrade configTrade;
	
	@Enumerated(EnumType.STRING)
	private ChangeType changeType;
	
	@Enumerated(EnumType.STRING)
	private UserState state;
	
	@Enumerated(EnumType.STRING)
	private TradeState stateTrade;
	
	private boolean tradeStartOrStop;
	
	@Builder
	public NodeUser(Long id, Long telegramUserId, Long chatId, Date firstLoginDate, String firstName,
			String lastName, String username, String email, Boolean isActive, Date lastStartTread,
			Account account, List<NodeOrder> orders, List<Account> accounts, ConfigTrade configTrade,
			ChangeType changeType, UserState state, TradeState stateTrade, boolean tradeStartOrStop) {
		this.id = id;
		this.telegramUserId = telegramUserId;
		this.chatId = chatId;
		this.firstLoginDate = firstLoginDate != null ? convertToLocalDateTime(firstLoginDate) : null;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.isActive = isActive;
		this.lastStartTread = lastStartTread != null ? convertToLocalDateTime(lastStartTread) : null;
		this.account = account;
		this.orders = orders;
		this.accounts = accounts;
		this.configTrade = configTrade;
		this.changeType = changeType;
		this.state = state;
		this.stateTrade = stateTrade;
		this.tradeStartOrStop = tradeStartOrStop;
	}
	
	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
}
