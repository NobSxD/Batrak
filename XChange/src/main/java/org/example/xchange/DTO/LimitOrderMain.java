package org.example.xchange.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Generated;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"limitPrice",
		"Order"
})
@Generated("jsonschema2pojo")
@Builder
@Jacksonized
@Value
public class LimitOrderMain {
	@JsonProperty("limitPrice")
	private BigDecimal limitPrice;
	@JsonProperty("Order")
	private OrderMain orderMain;
}