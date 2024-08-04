package org.example.castom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public final class MessageWrapperDTO {
	@JsonProperty("customMessage")
	CustomMessage customMessage;

	@JsonProperty("object")
	List<? extends Displayable> object;

	@JsonProperty("enumClass")
	Class<?> enumClass;

}
