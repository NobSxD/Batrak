package org.example.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.example.castom.Displayable;
import org.example.entity.collect.Pair;
import org.example.entity.enams.ChangeType;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeChange implements Displayable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToMany(mappedBy = "nodeChange", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Pair> pairs;


	@Enumerated(EnumType.STRING)
	@Column(name = "change_type", nullable = false)
	private ChangeType changeType;

	public void addPair(Pair pair) {
		pairs.add(pair);
		pair.setNodeChange(this);
	}

	@Override
	public String getDisplayName() {
		return changeType.name();
	}
}
