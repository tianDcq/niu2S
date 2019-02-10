package com.micro.game;


import frame.GameMain;

public class HCGameMain extends GameMain {
    public final int[] progress = { 40, 5, 30, 5, 20, 5, 10, 5 };

    public HCGameMain() {
        super();
    }

    @Override
    protected void onStart() {
        gameMgr = new HCGameMgr();

    }
}