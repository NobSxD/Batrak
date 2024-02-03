package com.example.node.entity;

import com.example.node.entity.enums.UserState;
import lombok.*;
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
@Table(name = "app_user")
@Entity
public class UserMain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Boolean isActive;



    @Enumerated(EnumType.STRING)
    private UserState state;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "userMain")
    private List <ApiKeys> apiKeys;
}
