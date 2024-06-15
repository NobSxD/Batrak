package org.example.entity.enams;

public enum UserState {


	//---------------- Запуск бота
	START,
	CHANGE,

	//----------------Базовое состояние
	BASIC_STATE,

	//--------------- команды бота
	CANCEL,
	HELP,

	//--------------- акаунт
	ACCOUNT_SELECTION,
	SELECT,
	INFO_ACCOUNT,

	//--------------- Почта
	EMAIL,
	WAIT_FOR_EMAIL_STATE,


	//---------------- Добавление аккаунта
	REGISTER_ACCOUNT,
	ACCOUNT_NAME,
	PUBLIC_API,
	SECRET_API,

	//---------------- Трейдинг
	MANAGER_TRADE,
	TRADE_START,
	TRADE_STOP,

	//---------------- Найстройка трейдинга
	NAME_PAIR,
	AMOUNT_ORDER,
	DEPTH_GLASS;
}
