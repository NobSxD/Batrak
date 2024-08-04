package org.example.castom;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.example.entity.Account;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Account.class, name = "account"),
		@JsonSubTypes.Type(value = ConfigTrade.class, name = "configTrade"),
		@JsonSubTypes.Type(value = NodeChange.class, name = "nodeChange"),
		@JsonSubTypes.Type(value = Pair.class, name = "pair")
})

public interface Displayable {
	String getDisplayName();
}
