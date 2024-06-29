package org.example.configuration;

import org.example.crypto.CryptoUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {


	@Bean
	public CryptoUtils cryptoUtils(){
		return new CryptoUtils();
	}
}
