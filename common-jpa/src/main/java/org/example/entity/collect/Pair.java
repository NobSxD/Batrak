package org.example.entity.collect;

import lombok.*;
import org.example.entity.NodeChange;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	String pair;

	@ManyToOne(fetch = FetchType.LAZY)
	private NodeChange nodeChange;
}
