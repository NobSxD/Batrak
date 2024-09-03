package org.example.entity;

import org.example.entity.enams.menu.MenuChange;
import org.example.entity.enams.state.TradeState;
import org.example.entity.enams.state.UserState;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
	private MenuChange menuChange;
	
	@Enumerated(EnumType.STRING)
	private UserState state;
	
	@Enumerated(EnumType.STRING)
	private TradeState stateTrade;
	
	private boolean tradeStartOrStop;
	
	@Builder
	public NodeUser(Long id, Long telegramUserId, Long chatId, Date firstLoginDate, String firstName,
			String lastName, String username, String email, Boolean isActive, Date lastStartTread,
			Account account, List<NodeOrder> orders, List<Account> accounts, ConfigTrade configTrade,
			MenuChange menuChange, UserState state, TradeState stateTrade, boolean tradeStartOrStop) {
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
		this.menuChange = menuChange;
		this.state = state;
		this.stateTrade = stateTrade;
		this.tradeStartOrStop = tradeStartOrStop;
	}
	
	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
}
