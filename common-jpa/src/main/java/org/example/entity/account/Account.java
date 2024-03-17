package org.example.entity.account;

import org.example.entity.NodeUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.*;

@Entity
@Inheritance(
		strategy = InheritanceType.TABLE_PER_CLASS
)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@NoRepositoryBean
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	private String nameChange;
	private String publicApiKey;
	private String secretApiKey;


	@ManyToOne(fetch = FetchType.LAZY)
	private NodeUser nodeUser;
}
