package com.micro.game.server.frame;

import com.micro.common.vo.GameRequestVO;

import lombok.Getter;
import lombok.Setter;

public abstract class Player extends Role{

    public abstract void onMsg(GameRequestVO object);

    public void sendMsg(GameRequestVO object)
    {
        
    }
}