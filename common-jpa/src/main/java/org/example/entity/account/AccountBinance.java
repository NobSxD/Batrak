package org.example.entity.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
public class AccountBinance extends Account{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
}
