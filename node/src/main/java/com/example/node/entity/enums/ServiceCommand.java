package com.example.node.entity.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    TRADESTART("/start_tread"),
    TRADESTOP("/stop_tread"),
    RESULT("/result"),
    PUBLIC_API_COMMAND("/public_key"),
    SECRET_API_COMMAND("/secret_key"),
    REGISTER_CHANGE("/registrationChange"),
    ACCOUNT("/account"),


    //акаунт команд
    INFO_ACCOUNT(" /infoBalance"),
    PAIR("/pair");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String v) {
        for (ServiceCommand c : ServiceCommand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
