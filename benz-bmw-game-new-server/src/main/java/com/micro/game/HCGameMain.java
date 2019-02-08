package com.micro.game;

import com.micro.frame.GameMain;

import org.springframework.stereotype.Component;


@Component
public class HCGameMain extends GameMain {
    public long repertory = 0;
    public final int[] progress = { 40, 5, 30, 5, 20, 5, 10, 5 };

    public HCGameMain() {
        super();
    }

    @Override
    protected void onStart() {
        gameMgr = new HCGameMgr();

    }
}