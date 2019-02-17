package com.micro.game;


import frame.GameMain;

public class TNGameMain extends GameMain {
    public TNGameMain() {
        super();
    }

    @Override
    protected void onStart() {
        gameMgr = new TNGameMgr();
    }
}