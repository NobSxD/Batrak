package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.example.entity.collect.ChangeType;
import org.example.entity.enams.state.TradeState;
import org.example.entity.enams.state.UserState;
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
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Boolean isActive;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime firstLoginDate;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastStartTread;

    @OneToOne
    @JsonManagedReference
    protected Account account;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "nodeUser")
    @JsonIgnore
    private List<NodeOrder> orders;

    @OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true, mappedBy = "nodeUser")
    @JsonIgnore
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


    @Builder
    public NodeUser(Long id, Long telegramUserId, Long chatId, Date firstLoginDate, String firstName,
                    String lastName, String username, String email, Boolean isActive, Date lastStartTread,
                    Account account, List<NodeOrder> orders, List<Account> accounts, ConfigTrade configTrade,
                    ChangeType changeType, UserState state, TradeState stateTrade) {
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
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
