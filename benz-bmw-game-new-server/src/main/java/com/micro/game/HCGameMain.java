package com.micro.game;

import com.micro.frame.GameMain;

public class HCGameMain extends GameMain {

    public HCGameMain() {
        super();
    }

    protected void onStart() {
        gameMgr = new HCGameMgr();
    }
}