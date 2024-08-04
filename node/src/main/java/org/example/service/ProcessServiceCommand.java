package org.example.service;

import org.example.entity.NodeUser;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProcessServiceCommand {

	String helpAccount();
	NodeUser findOrSaveAppUser(Update update);

}
