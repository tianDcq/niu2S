package com.micro.game;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.Robot;
import com.micro.frame.socket.Response;

import lombok.Getter;

class HCRobot extends Robot implements HCRoleInterface {
    public @Getter ChipStruct[] chipList=new ChipStruct[8];
    public int rChipTime;
    public void endGame(){
        for(int i=0;i<8;++i){
            chipList[i].betAmount=0;
        }
    }
    public void sendMsg(Response res) {
        String msgType=res.msgType;
        if(msgType=="2012"){
            
        }
    }
    public void chip(){
        Map<String, Object> roomConfig = room.getRoomConfig();
        
    }
}