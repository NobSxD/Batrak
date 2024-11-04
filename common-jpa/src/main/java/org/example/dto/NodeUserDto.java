package org.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.castom.Displayable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeUserDto implements Displayable {
    @JsonProperty("userName")
    private String userName;
    @Override
    public String getDisplayName() {
        return userName;
    }

    @JsonCreator
    public NodeUserDto(@JsonProperty("userName") String userName) {
        this.userName = userName;
    }
}
