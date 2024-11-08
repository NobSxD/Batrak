package org.exampel.crypto;

import org.jasypt.util.text.BasicTextEncryptor;


public class CryptoUtils {

	private final BasicTextEncryptor encryptor;

	public CryptoUtils() {
		encryptor = new BasicTextEncryptor();
		encryptor.setPassword(System.getenv("SECRET_KEY_SALT"));
	}

	public String encryptMessage(String message) {
		return encryptor.encrypt(message);
	}

	public String decryptMessage(String encryptedMessage) {
		return encryptor.decrypt(encryptedMessage);
	}

}
