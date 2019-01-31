package com.micro.game.server.game;

import com.micro.game.server.frame.*;

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