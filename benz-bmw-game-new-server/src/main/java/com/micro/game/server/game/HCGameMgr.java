package com.micro.game.server.game;

import com.micro.game.server.frame.*;

class HCGameMgr implements GameMgrInterface {
    public Player createPlayer() {
        return new HCPlayer();
    }

    public Robot createRobot() {
        return new HCRobot();
    }

    public Table createTable(float time) {
        return new HCTable(time);
    }
}