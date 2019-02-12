package com.micro.game;


import frame.GameMain;

public class HCGameMain extends GameMain {
    public HCGameMain() {
        super();
    }

    @Override
    protected void onStart() {
        gameMgr = new HCGameMgr();

    }
}