package org.example.entity;

import lombok.*;
import org.example.entity.collect.Pair;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeChange {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToMany(mappedBy = "pair")
	protected List<Pair> pairs;


	@OneToOne
	protected Account account;

	@OneToMany
	protected List<NodeUser> nodeUser;
}
