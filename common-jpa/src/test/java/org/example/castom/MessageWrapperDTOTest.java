package org.example.castom;

import org.example.entity.ConfigTrade;
import org.example.entity.enams.menu.MenuStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessageWrapperDTOTest {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void testSerialization() throws JsonProcessingException {
		CustomMessage customMessage = CustomMessage.builder()
												   .chatId("123")
												   .text("Test Message")
												   .build();
		ConfigTrade configTrade = new ConfigTrade(); // Добавьте все необходимые поля
		configTrade.setNamePair("BTC-USDT");
		configTrade.setAmountOrder(new BigDecimal("11"));
		
		MessageWrapperDTO messageWrapper = MessageWrapperDTO.builder()
															.customMessage(customMessage)
															.object(Collections.singletonList(configTrade))
															.enumClass(MenuStrategy.class)
															.build();
		
		String jsonString = objectMapper.writeValueAsString(messageWrapper);
		
		assertNotNull(jsonString);
		System.out.println("Serialized JSON: " + jsonString);
	}
	
	@Test
	public void testDeserialization() throws JsonProcessingException {
		String jsonString = "{\"customMessage\":{\"text\":\"Test Message\"}," +
				"\"object\":[{\"type\":\"configTrade\",\"namePair\":\"BTC-USDT\",\"amountOrder\":11,\"depthGlass\":100,\"realTrade\":true,\"strategy\":\"SlidingProtectiveOrder\"}]," +
				"\"enumClass\":\"org.example.MenuStrategy\"}";
		
		MessageWrapperDTO messageWrapper = objectMapper.readValue(jsonString, MessageWrapperDTO.class);
		
		assertNotNull(messageWrapper);
		assertEquals("Test Message", messageWrapper.getCustomMessage().getText());
		assertEquals(1, messageWrapper.getObject().size());
		assertEquals(ConfigTrade.class, messageWrapper.getObject().get(0).getClass());
		assertEquals(MenuStrategy.class, messageWrapper.getEnumClass());
	}
	
}