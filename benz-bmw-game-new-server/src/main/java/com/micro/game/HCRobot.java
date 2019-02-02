package com.micro.game;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.Robot;
import com.micro.frame.socket.Response;

import lombok.Getter;

class HCRobot extends Robot implements HCRoleInterface {
    public @Getter ChipStruct[] chipList=new ChipStruct[8];
    public int rChipTime;
    public int minChip;
    public int maxChip;
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

    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        minChip=(int)roomConfig.get("bottomRed1");
        maxChip=(int)roomConfig.get("bottomRed2");
	}


    public void chip(){
        
    }
}