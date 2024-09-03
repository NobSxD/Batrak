package org.example.castom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMessage{
	@JsonProperty("chat_id")
	private  String chatId;

	@JsonProperty("text")
	private  String text;

}
