package org.example.entity.collect;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.example.entity.NodeChange;

import jakarta.persistence.*;

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
	@JoinColumn(name = "node_change_id")
	@JsonBackReference
	private NodeChange nodeChange;

	public Pair(String pair) {
		this.pair = pair;
	}
}
