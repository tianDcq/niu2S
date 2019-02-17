package com.micro.game;

import java.util.List;

import frame.Robot;
import frame.socket.BaseResponse;
import lombok.Getter;
import lombok.Setter;


class TNRobot extends Robot implements TNRoleInterface {
    public @Getter @Setter int bankNum;
    public @Getter @Setter int sit;
    public @Getter @Setter int chipNum;
    public @Getter @Setter List<Integer> cards;
    public @Getter @Setter int playerState;    //1叫  2 叫完 3选分 4等待选分 5开牌 6等待 0未匹配
    @Override
    public void endGame() {
  
    }
    @Override
    public void send(BaseResponse res) {
        String msgType = res.msgType;
    }
}