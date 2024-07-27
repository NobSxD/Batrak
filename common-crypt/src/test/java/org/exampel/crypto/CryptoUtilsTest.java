package org.exampel.crypto;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CryptoUtilsTest {
	private  CryptoUtils encryptor;

	public CryptoUtilsTest() {
		this.encryptor = new CryptoUtils();
	}

	@BeforeEach
	public void setUp() {
		this.encryptor = new CryptoUtils();
	}


	@Test
	void testEncryptMessage() {
		String message = "Hello, world!";
		String encryptedMessage = encryptor.encryptMessage(message);
		System.out.println(encryptedMessage);
		String decryptedMessage = encryptor.decryptMessage(encryptedMessage);
		assertEquals("Hello, world!", decryptedMessage);
	}

}