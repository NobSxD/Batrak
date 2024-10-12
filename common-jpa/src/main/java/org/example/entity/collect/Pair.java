package org.example.entity.collect;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

	String namePair;
	int scale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "node_change_id")
	@JsonBackReference
	private NodeChange nodeChange;

	public Pair(String namePair, int scale) {
		this.namePair = namePair;
		this.scale = scale;
	}

	@Override
	public String getDisplayName() {
		return namePair;
	}
}
