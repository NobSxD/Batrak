package org.example.service.impl;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoggerInFile {

	@SneakyThrows
	public static void saveLogInFile(String error ,String nameFile){

			File file = new File("D:/project/new/treadeBot/node/src/main/resources/log/");
			file.mkdirs();

			File raw = new File(file, nameFile);


			if (! raw.exists()) {
				raw.createNewFile();
			}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(raw, true);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

			String rawDataString = error + "\n";
		try {
			fileOutputStream.write(rawDataString.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
