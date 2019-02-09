package com.micro.game;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document
class GameID_game implements Serializable{
    @Indexed
    public String gameId;
    public String roomName;
    public long startTime;
    public long endTime;
    public String tax;
    public int open;
    public String sysHost;
    public Map<String,Long> wins;          //结算结果 id-money
    public Map<String,Long> opens;         //中将结果 id-money
    public ChipStruct[] chipList;      //总下注 id-ChipStruct
    public Map<String,Object> playerbetParts;    //geren id-ChipStruct
}