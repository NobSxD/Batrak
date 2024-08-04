package org.example.castom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CustomMessage{
	@JsonProperty("chat_id")
	private final String chatId;

	@JsonProperty("text")
	private final String text;

}
