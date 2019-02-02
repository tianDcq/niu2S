package com.micro.game;

import com.micro.frame.*;

class HCGameMgr extends GameMgr {
    HCGameMgr() {
        robotPairType = new Config.RobotPairType(Config.RobotPairType.Type.One, 10, 20);
        gameId = 1;
    }

    public Player createPlayer() {
        return new HCPlayer();
    }

    public Robot createRobot() {
        return new HCRobot();
    }

    public Table createTable() {
        return new HCTable();
    }
}