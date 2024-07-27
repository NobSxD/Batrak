package org.example.xchange.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public final class ChangeUser {
	private String userName;
	private String botName;
	private String apiKey;
	private String secretKey;
	private String pairName;

}
