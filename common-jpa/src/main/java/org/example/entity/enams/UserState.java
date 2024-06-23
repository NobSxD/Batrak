package org.example.entity.enams;

public enum UserState {


	//---------------- Запуск бота
	BOT_START,
	BOT_CHANGE,

	//----------------Базовое состояние
	BASIC_STATE,

	//--------------- команды бота
	BOT_CANCEL,
	BOT_HELP,
	BOT_BACK,
	BOT_MAIN_MENU,

	//--------------- акаунт
	ACCOUNT_LIST,
	ACCOUNT_SELECT,
	ACCOUNT_INFO,
	ACCOUNT_DELETE,

	//---------------- Добавление аккаунта
	ACCOUNT_ADD_REGISTER,
	ACCOUNT_ADD_NAME,
	ACCOUNT_ADD_PUBLIC_API,
	ACCOUNT_ADD_SECRET_API,

	//--------------- Почта
	MAIL_EMAIL,
	MAIL_WAIT_FOR_EMAIL_STATE,


	//---------------- Трейдинг
	TRADE_MANAGER,
	TRADE_START,
	TRADE_STOP,

	//---------------- Найстройка трейдинга
	SETTINGS_NAME_PAIR,
	SETTINGS_SAVE_NAME_PAIR,
	SETTINGS_AMOUNT_ORDER,
	SETTINGS_SAVE_AMOUNT_ORDER,
	SETTINGS_DEPTH_GLASS,
	SETTINGS_SAVE_DEPTH_GLASS,
	SETTINGS_NAME_STRATEGY,
	SETTINGS_SAVE_NAME_STRATEGY;
}
