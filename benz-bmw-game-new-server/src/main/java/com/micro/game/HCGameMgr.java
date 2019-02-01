package com.micro.game;

import com.micro.frame.*;

class HCGameMgr implements GameMgrInterface {
    public Player createPlayer() {
        return new HCPlayer();
    }

    public Robot createRobot() {
        return new HCRobot();
    }

    public Table createTable() {
        return new HCTable();
    }

    public void onTerminate() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }
}