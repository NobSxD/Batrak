package org.example.entity;

import org.example.castom.Displayable;
import org.example.entity.collect.ChangeType;
import org.example.entity.collect.Pair;

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

import java.util.Map;

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
	private Map<String,Pair> pairs;


	@Enumerated(EnumType.STRING)
	@Column(name = "change_type", nullable = false)
	@JsonProperty("changeType")
	private ChangeType changeType;

	public void addPair(Pair pair) {
		pairs.put(pair.getNamePair(), pair);
		pair.setNodeChange(this);
	}

	@Override
	public String getDisplayName() {
		return changeType.name();
	}
}
