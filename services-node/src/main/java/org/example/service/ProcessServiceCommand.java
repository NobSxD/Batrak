package org.example.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import org.example.entity.NodeUser;

public interface ProcessServiceCommand {
	NodeUser findOrSaveAppUser(Update update);
	long extractChatIdFromUpdate(Update update);

}
