package org.example.entity.collect;

import org.example.castom.Displayable;
import org.example.entity.NodeChange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@JsonIgnore
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
