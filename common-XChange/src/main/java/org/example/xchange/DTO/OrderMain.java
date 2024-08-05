package org.example.xchange.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"type",
		"originalAmount",
		"cumulativeAmount",
		"averagePrice",
		"fee",
		"instrument",
		"id",
		"timestamp",
		"status",
		"flags",
		"userReference"
})
@Generated("jsonschema2pojo")
@Builder
@Jacksonized
@Value
public final class OrderMain {

	@JsonProperty("type")
	private Order.OrderType type;

	@JsonProperty("originalAmount")
	private BigDecimal originalAmount;

	@JsonProperty("cumulativeAmount")
	private BigDecimal cumulativeAmount;

	@JsonProperty("averagePrice")
	private BigDecimal averagePrice;

	@JsonProperty("fee")
	private Object fee;
	@JsonProperty("instrument")
	private Instrument instrument;

	@JsonProperty("id")
	private String id;

	@JsonProperty("timestamp")
	private LocalDateTime timestamp;

	@JsonProperty("status")
	private Object status;

	@JsonProperty("flags")
	private List<Object> flags;

	@JsonProperty("userReference")
	private String userReference;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();



}