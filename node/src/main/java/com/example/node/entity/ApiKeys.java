package com.example.node.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameChange;

    private String publicApiKey;
    private String secretApiKey;

    @ManyToOne
    @JoinColumn(name = "userMain")
    private UserMain userMain;


}
