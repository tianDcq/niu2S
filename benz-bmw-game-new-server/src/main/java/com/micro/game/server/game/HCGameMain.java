package com.micro.game.server.game;

import com.micro.game.server.frame.GameMain;

public class HCGameMain extends GameMain {

    public HCGameMain() {
        super();
    }

    protected void onStart() {
        gameMgr = new HCGameMgr();
    }
}