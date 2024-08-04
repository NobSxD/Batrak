package org.example.entity.collect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.castom.Displayable;
import org.example.entity.NodeChange;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pair implements Displayable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	String pair;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "node_change_id")
	@JsonIgnore
	private NodeChange nodeChange;

	public Pair(String pair) {
		this.pair = pair;
	}

	@Override
	public String getDisplayName() {
		return pair;
	}
}
