package com.micro.game;

import com.micro.frame.*;

class HCGameMgr extends GameMgr {
    HCGameMgr() {
        robotPairType = new Config.RobotPairType(Config.RobotPairType.Type.One, 0, 0);
        gameId = 1;
    }

    @Override
    public Player createPlayer() {
        return new HCPlayer();
    }

    @Override
    public Robot createRobot() {
        return new HCRobot();
    }

    @Override
    public Table createTable() {
        return new HCTable();
    }
}