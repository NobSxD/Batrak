package org.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.castom.Displayable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeUserDto implements Displayable {
    @JsonProperty("id")
    private long id;

    @JsonProperty("chatId")
    private long chatId;
    @JsonProperty("userName")
    private String userName;

    @Override
    public String getDisplayName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    @JsonCreator
    public NodeUserDto(@JsonProperty("id") long id, @JsonProperty("chatId") long chatId, @JsonProperty("userName") String userName) {
        this.userName = userName;
        this.id = id;
        this.chatId = chatId;
    }
}
