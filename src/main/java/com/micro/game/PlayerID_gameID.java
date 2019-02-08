package com.micro.game;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
class PlayerID_gameID{
    @Id
    private String id;
    @Indexed
    public String playerID;
    public String gameID;
}