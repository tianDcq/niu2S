package com.micro.game;

import com.micro.frame.*;

class HCGameMgr implements GameMgrInterface {
    public Player createPlayer(String uniqueId) {
        return new HCPlayer(uniqueId);
    }

    public Robot createRobot(String uniqueId) {
        return new HCRobot(uniqueId);
    }

    public Table createTable(float time) {
        return new HCTable(time);
    }
}