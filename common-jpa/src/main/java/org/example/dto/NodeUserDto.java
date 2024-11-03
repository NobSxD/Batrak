package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.castom.Displayable;

public class NodeUserDto implements Displayable {
    @JsonProperty("userName")
    private String userName;
    @Override
    public String getDisplayName() {
        return userName;
    }

    public NodeUserDto(String userName) {
        this.userName = userName;
    }
}
