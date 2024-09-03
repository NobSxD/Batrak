package org.example.castom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class MessageWrapperDTO {
	@JsonProperty("customMessage")
	CustomMessage customMessage;

	@JsonProperty("object")
	List<? extends Displayable> object;

	@JsonProperty("enumClass")
	Class<?> enumClass;

}
