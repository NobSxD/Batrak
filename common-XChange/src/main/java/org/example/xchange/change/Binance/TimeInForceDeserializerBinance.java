package org.example.xchange.change.Binance;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.knowm.xchange.binance.dto.trade.TimeInForce;

import java.io.IOException;

public class TimeInForceDeserializerBinance extends JsonDeserializer<TimeInForce> {
    @Override
    public TimeInForce deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String timeInForceValue = p.getText();
        // Логика для создания TimeInForce из строки
        return TimeInForce.getTimeInForce(timeInForceValue);
    }
}
