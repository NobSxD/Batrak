package org.example.crypto;

import org.jasypt.util.text.BasicTextEncryptor;



public class CryptoUtils {


	private BasicTextEncryptor encryptor = new BasicTextEncryptor();

	public CryptoUtils() {
		encryptor.setPassword(System.getenv("secretKey"));
	}

	public String encryptMessage(String message) {
		return encryptor.encrypt(message);
	}

	public String decryptMessage(String encryptedMessage) {
		return encryptor.decrypt(encryptedMessage);
	}

}
