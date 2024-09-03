package org.example.entity;

import org.example.castom.Displayable;
import org.example.entity.collect.Pair;
import org.example.entity.enams.menu.MenuChange;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@JsonProperty("menuChange")
	private MenuChange menuChange;

	public void addPair(Pair pair) {
		pairs.add(pair);
		pair.setNodeChange(this);
	}

	@Override
	public String getDisplayName() {
		return menuChange.name();
	}
}
