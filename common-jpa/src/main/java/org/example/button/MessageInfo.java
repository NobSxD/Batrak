package org.example.button;

public class MessageInfo {
    public static final String USER_NOT_FOUND = "Пользователь не найден, введите команду /start";
    public static final String EXCHANGE_NOT_FOUND = "Биржа не найдена, введите команду /start";
    public static final String ACCOUNT_NOT_SELECTED = "Аккаунт не выбран";
    public static final String SETTINGS_ACCOUNT_NOT_FOUND = "Настройки аккаунта не найдены";

    public static final String DEMO_MODE_ACTIVE =
            "Внимание! В настоящий момент активирован виртуальный режим трейдинга. " +
                    "Все операции будут производиться без использования реального счета.";

    public static final String REAL_MODE_ACTIVE =
            "Внимание! В настоящее время активирован реальный режим трейдинга. " +
                    "Все операции будут производиться с использованием реального счета.";

    public static final String CURRENT_SETTINGS_HEADER = "[Текущие настройки]\n";
    public static final String CURRENT_ACCOUNT = "Текущий аккаунт: ";
    public static final String EXCHANGE = "Биржа: ";
    public static final String ORDER_AMOUNT = "Сумма ордера: $";
    public static final String CURRENCY_PAIR = "Валютная пара: ";
    public static final String STEP_BAY = "Шаг покупки: ";
    public static final String STEP_SELL = "Шаг продажи: ";
    public static final String DEPOSIT = "Депозит: ";
    public static final String SETTINGS_SEPARATOR = "\n------------------------------------\n";
    public static final String SERVER_IP_ADDRESS = "\nip адрес сервера: ";
    public static final String SELL_PRICE_STEP_CHANGED_SELL =
            "Шаг цены продажи успешно изменен. Новый шаг: %s%s";
    public static final String SELL_PRICE_STEP_CHANGED_BAY =
            "Шаг цены продажи успешно изменен. Новый шаг: %s%s";
    public static final String ENTER_PRICE_STEP =
            "Укажите шаг цены, пример '0.5' процентов";
    public static final String PRICE_STEP_MENU_ERROR =
            "При переходе в меню изменение шага цены произошла ошибка, обратитесь к администратору";
    public static final String ENTER_DEPOSIT_AMOUNT =
            "Укажите размер депозита: ";
    public static final String DEPOSIT_MENU_ERROR =
            "При переходе в меню, изменение размер депозита произошла ошибка, обратитесь к администратору";
    public static final String DEPOSIT_AMOUNT_CHANGED =
            "Размер депозита успешно изменена. Депозит составляет: $%s";
    public static final String NUMBER_FORMAT_EXCEPTION = "Ведите пожалуйсто число";

    //метод send
    public static final String USER_NOT_FOUND_MESSAGE = "Мы не смогли найти вас в системе, введите команду /start и попробуйте снова.";
    public static final String UNKNOWN_COMMAND_MESSAGE_TEMPLATE = "Команда %s не найдена.";
    public static final String NO_ACCESS_MESSAGE = "У вас нет доступа";
    public static final String ENCRYPTION_ERROR_MESSAGE = "Ошибка при шифровании пароля";
    public static final String DATABASE_SAVE_ERROR_MESSAGE = "Ошибка при сохранении в базу данных";

}
